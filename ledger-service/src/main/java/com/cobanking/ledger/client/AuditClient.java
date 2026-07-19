package com.cobanking.ledger.client;

import com.cobanking.ledger.config.LedgerServiceProperties;
import com.cobanking.ledger.entity.LedgerBatch;
import com.cobanking.ledger.entity.LedgerEntry;
import java.math.BigDecimal;
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

    public AuditClient(RestClient.Builder restClientBuilder, LedgerServiceProperties properties) {
        this.restClient = restClientBuilder
                .baseUrl(properties.baseUrl())
                .build();
    }

    public void recordLedgerBatchPosted(LedgerBatch batch) {
        try {
            restClient.post()
                    .uri("/audit/events")
                    .body(RecordAuditEventRequest.forLedgerBatchPosted(batch))
                    .retrieve()
                    .toBodilessEntity();
        } catch (RestClientException exception) {
            LOGGER.warn("Failed to record LEDGER_BATCH_POSTED audit event for batch {}", batch.getId(), exception);
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
        static RecordAuditEventRequest forLedgerBatchPosted(LedgerBatch batch) {
            String metadata = """
                    {"referenceId":"%s","source":"%s","currency":"%s","entryCount":%d,"debitTotal":%s,"creditTotal":%s}
                    """.formatted(
                    batch.getReferenceId(),
                    batch.getSource(),
                    batch.getCurrency(),
                    batch.getEntries().size(),
                    total(batch, "DEBIT"),
                    total(batch, "CREDIT"));

            return new RecordAuditEventRequest(
                    batch.getTenantId(),
                    "ledger-service",
                    "LEDGER_BATCH_POSTED",
                    "LEDGER_BATCH",
                    batch.getId(),
                    metadata.strip());
        }

        private static BigDecimal total(LedgerBatch batch, String entryType) {
            return batch.getEntries().stream()
                    .filter(entry -> entry.getEntryType().name().equals(entryType))
                    .map(LedgerEntry::getAmount)
                    .reduce(BigDecimal.ZERO, BigDecimal::add);
        }
    }
}
