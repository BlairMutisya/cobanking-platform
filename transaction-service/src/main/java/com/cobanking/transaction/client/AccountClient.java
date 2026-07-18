package com.cobanking.transaction.client;

import com.cobanking.transaction.config.TransactionServiceProperties;
import java.time.Instant;
import java.util.UUID;
import org.springframework.stereotype.Component;
import org.springframework.web.client.RestClient;
import org.springframework.web.client.RestClientException;

@Component
public class AccountClient {
    private final RestClient restClient;

    public AccountClient(RestClient.Builder restClientBuilder, TransactionServiceProperties properties) {
        this.restClient = restClientBuilder
                .baseUrl(properties.account().baseUrl())
                .build();
    }

    public AccountValidationResponse validateAccount(UUID tenantId, UUID accountId) {
        AccountValidationApiResponse response = restClient.get()
                .uri(uriBuilder -> uriBuilder
                        .path("/accounts/{accountId}/validation")
                        .queryParam("tenantId", tenantId)
                        .build(accountId))
                .retrieve()
                .body(AccountValidationApiResponse.class);

        if (response == null || response.data() == null) {
            throw new RestClientException("Account service returned an empty validation response");
        }

        return response.data();
    }

    public record AccountValidationResponse(
            UUID accountId,
            UUID tenantId,
            String currency,
            String status,
            boolean valid,
            String reason
    ) {
    }

    private record AccountValidationApiResponse(
            int status,
            String message,
            AccountValidationResponse data,
            Instant timestamp
    ) {
    }
}
