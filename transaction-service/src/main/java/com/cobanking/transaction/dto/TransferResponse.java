package com.cobanking.transaction.dto;

import com.cobanking.transaction.domain.TransactionStatus;
import java.math.BigDecimal;
import java.time.Instant;
import java.util.UUID;

public record TransferResponse(
        UUID transactionId,
        UUID tenantId,
        String idempotencyKey,
        UUID debitAccountId,
        UUID creditAccountId,
        BigDecimal amount,
        String currency,
        TransactionStatus status,
        UUID ledgerBatchId,
        String failureReason,
        Instant createdAt
) {
}
