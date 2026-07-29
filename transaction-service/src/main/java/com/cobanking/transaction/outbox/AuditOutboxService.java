package com.cobanking.transaction.outbox;

import com.cobanking.transaction.client.AuditClient;
import com.cobanking.transaction.entity.TransferTransaction;
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

    public void recordTransferEvent(String action, TransferTransaction transaction) {
        String metadata = """
                {"debitAccountId":"%s","creditAccountId":"%s","amount":%s,"currency":"%s","status":"%s","ledgerBatchId":"%s","failureReason":"%s"}
                """.formatted(
                transaction.getDebitAccountId(),
                transaction.getCreditAccountId(),
                transaction.getAmount(),
                transaction.getCurrency(),
                transaction.getStatus(),
                transaction.getLedgerBatchId(),
                transaction.getFailureReason());

        auditOutboxRepository.save(new AuditOutboxEvent(
                transaction.getTenantId(),
                "transaction-service",
                action,
                "TRANSFER_TRANSACTION",
                transaction.getId(),
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
}
