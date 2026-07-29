package com.cobanking.ledger.client;

import com.cobanking.ledger.config.LedgerServiceProperties;
import com.cobanking.ledger.outbox.AuditOutboxEvent;
import java.util.UUID;
import org.springframework.stereotype.Component;
import org.springframework.web.client.RestClient;

@Component
public class AuditClient {
    private final RestClient restClient;

    public AuditClient(RestClient.Builder restClientBuilder, LedgerServiceProperties properties) {
        this.restClient = restClientBuilder
                .baseUrl(properties.baseUrl())
                .build();
    }

    public void publish(AuditOutboxEvent event) {
        restClient.post()
                .uri("/audit/events")
                .body(RecordAuditEventRequest.from(event))
                .retrieve()
                .toBodilessEntity();
    }

    private record RecordAuditEventRequest(
            UUID tenantId,
            String actor,
            String action,
            String resource,
            UUID resourceId,
            String metadata
    ) {
        static RecordAuditEventRequest from(AuditOutboxEvent event) {
            return new RecordAuditEventRequest(
                    event.getTenantId(),
                    event.getActor(),
                    event.getAction(),
                    event.getResource(),
                    event.getResourceId(),
                    event.getMetadata());
        }
    }
}
