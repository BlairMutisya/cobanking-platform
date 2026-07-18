package com.cobanking.transaction.client;

import com.cobanking.transaction.config.TransactionServiceProperties;
import com.cobanking.transaction.entity.TransferTransaction;
import java.util.UUID;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;
import org.springframework.web.client.RestClient;
import org.springframework.web.client.RestClientException;

@Component
public class AuditClient {
    private static final Logger LOGGER = LoggerFactory.getLogger(AuditClient.class);

    private final RestClient restClient;

    public AuditClient(RestClient.Builder restClientBuilder, TransactionServiceProperties properties) {
        this.restClient = restClientBuilder
                .baseUrl(properties.audit().baseUrl())
                .build();
    }

    public void recordTransferEvent(String action, TransferTransaction transaction) {
        try {
            restClient.post()
                    .uri("/audit/events")
                    .body(RecordAuditEventRequest.forTransfer(action, transaction))
                    .retrieve()
                    .toBodilessEntity();
        } catch (RestClientException exception) {
            LOGGER.warn("Failed to record {} audit event for transaction {}", action, transaction.getId(), exception);
        }
    }

    private record RecordAuditEventRequest(
            UUID tenantId,
            String actor,
            String action,
            String resource,
            UUID resourceId,
            String metadata
    ) {
        static RecordAuditEventRequest forTransfer(String action, TransferTransaction transaction) {
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

            return new RecordAuditEventRequest(
                    transaction.getTenantId(),
                    "transaction-service",
                    action,
                    "TRANSFER_TRANSACTION",
                    transaction.getId(),
                    metadata.strip());
        }
    }
}
