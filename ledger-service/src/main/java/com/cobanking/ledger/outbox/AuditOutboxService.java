package com.cobanking.ledger.outbox;

import com.cobanking.ledger.client.AuditClient;
import com.cobanking.ledger.entity.LedgerBatch;
import com.cobanking.ledger.entity.LedgerEntry;
import java.math.BigDecimal;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
public class AuditOutboxService {
    private final AuditOutboxRepository auditOutboxRepository;
    private final AuditClient auditClient;

    public AuditOutboxService(AuditOutboxRepository auditOutboxRepository, AuditClient auditClient) {
        this.auditOutboxRepository = auditOutboxRepository;
        this.auditClient = auditClient;
    }

    public void recordLedgerBatchPosted(LedgerBatch batch) {
        String metadata = """
                {"referenceId":"%s","source":"%s","currency":"%s","entryCount":%d,"debitTotal":%s,"creditTotal":%s}
                """.formatted(
                batch.getReferenceId(),
                batch.getSource(),
                batch.getCurrency(),
                batch.getEntries().size(),
                total(batch, "DEBIT"),
                total(batch, "CREDIT"));

        auditOutboxRepository.save(new AuditOutboxEvent(
                batch.getTenantId(),
                "ledger-service",
                "LEDGER_BATCH_POSTED",
                "LEDGER_BATCH",
                batch.getId(),
                metadata.strip()));
    }

    @Scheduled(fixedDelayString = "${cobanking.outbox.audit.publish-delay-ms:5000}")
    @Transactional
    public void publishPendingEvents() {
        auditOutboxRepository.findTop20ByStatusOrderByCreatedAtAsc("PENDING")
                .forEach(this::publish);
    }

    private void publish(AuditOutboxEvent event) {
        try {
            auditClient.publish(event);
            event.markPublished();
        } catch (RuntimeException exception) {
            event.markFailed(exception.getMessage());
        }
    }

    private static BigDecimal total(LedgerBatch batch, String entryType) {
        return batch.getEntries().stream()
                .filter(entry -> entry.getEntryType().name().equals(entryType))
                .map(LedgerEntry::getAmount)
                .reduce(BigDecimal.ZERO, BigDecimal::add);
    }
}
