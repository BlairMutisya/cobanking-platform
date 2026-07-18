package com.cobanking.transaction.client;

import com.cobanking.transaction.config.TransactionServiceProperties;
import com.cobanking.transaction.domain.TransferTransaction;
import java.math.BigDecimal;
import java.time.Instant;
import java.util.List;
import java.util.UUID;
import org.springframework.stereotype.Component;
import org.springframework.web.client.RestClient;
import org.springframework.web.client.RestClientException;

@Component
public class LedgerClient {
    private final RestClient restClient;

    public LedgerClient(RestClient.Builder restClientBuilder, TransactionServiceProperties properties) {
        this.restClient = restClientBuilder
                .baseUrl(properties.baseUrl())
                .build();
    }

    public LedgerBatchResponse postTransfer(TransferTransaction transaction) {
        LedgerApiResponse response = restClient.post()
                .uri("/ledger/transfers")
                .body(PostTransferLedgerRequest.from(transaction))
                .retrieve()
                .body(LedgerApiResponse.class);

        if (response == null || response.data() == null) {
            throw new RestClientException("Ledger service returned an empty response");
        }

        return response.data();
    }

    private record PostTransferLedgerRequest(
            UUID tenantId,
            UUID referenceId,
            UUID debitAccountId,
            UUID creditAccountId,
            BigDecimal amount,
            String currency
    ) {
        static PostTransferLedgerRequest from(TransferTransaction transaction) {
            return new PostTransferLedgerRequest(
                    transaction.getTenantId(),
                    transaction.getId(),
                    transaction.getDebitAccountId(),
                    transaction.getCreditAccountId(),
                    transaction.getAmount(),
                    transaction.getCurrency());
        }
    }

    public record LedgerBatchResponse(
            UUID batchId,
            UUID tenantId,
            UUID referenceId,
            String source,
            String currency,
            Instant postedAt,
            List<LedgerEntryResponse> entries
    ) {
    }

    public record LedgerEntryResponse(
            UUID entryId,
            UUID accountId,
            String entryType,
            BigDecimal amount
    ) {
    }

    private record LedgerApiResponse(
            boolean success,
            String message,
            LedgerBatchResponse data,
            Instant timestamp
    ) {
    }
}
