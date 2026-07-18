package com.cobanking.account.client;

import com.cobanking.account.config.AccountServiceProperties;
import com.cobanking.account.entity.Account;
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

    public AuditClient(RestClient.Builder restClientBuilder, AccountServiceProperties properties) {
        this.restClient = restClientBuilder
                .baseUrl(properties.baseUrl())
                .build();
    }

    public void recordAccountOpened(Account account) {
        try {
            restClient.post()
                    .uri("/audit/events")
                    .body(RecordAuditEventRequest.forAccountOpened(account))
                    .retrieve()
                    .toBodilessEntity();
        } catch (RestClientException exception) {
            LOGGER.warn("Failed to record ACCOUNT_OPENED audit event for account {}", account.getId(), exception);
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
        static RecordAuditEventRequest forAccountOpened(Account account) {
            String metadata = """
                    {"accountNumber":"%s","customerId":"%s","currency":"%s","accountType":"%s"}
                    """.formatted(
                    account.getAccountNumber(),
                    account.getCustomerId(),
                    account.getCurrency(),
                    account.getAccountType());

            return new RecordAuditEventRequest(
                    account.getTenantId(),
                    "account-service",
                    "ACCOUNT_OPENED",
                    "ACCOUNT",
                    account.getId(),
                    metadata.strip());
        }
    }
}
