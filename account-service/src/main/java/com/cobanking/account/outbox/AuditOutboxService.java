package com.cobanking.account.outbox;

import com.cobanking.account.client.AuditClient;
import com.cobanking.account.entity.Account;
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

    public void recordAccountOpened(Account account) {
        String metadata = """
                {"accountNumber":"%s","customerId":"%s","currency":"%s","accountType":"%s"}
                """.formatted(
                account.getAccountNumber(),
                account.getCustomerId(),
                account.getCurrency(),
                account.getAccountType());

        auditOutboxRepository.save(new AuditOutboxEvent(
                account.getTenantId(),
                "account-service",
                "ACCOUNT_OPENED",
                "ACCOUNT",
                account.getId(),
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
