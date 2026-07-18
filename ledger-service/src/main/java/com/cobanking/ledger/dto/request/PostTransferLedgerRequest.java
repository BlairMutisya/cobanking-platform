package com.cobanking.ledger.dto.request;

import jakarta.validation.constraints.DecimalMin;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Pattern;
import java.math.BigDecimal;
import java.util.UUID;

public record PostTransferLedgerRequest(
        @NotNull UUID tenantId,
        @NotNull UUID referenceId,
        @NotNull UUID debitAccountId,
        @NotNull UUID creditAccountId,
        @NotNull @DecimalMin("0.01") BigDecimal amount,
        @NotBlank @Pattern(regexp = "^[A-Z]{3}$", message = "currency must be a 3-letter ISO code") String currency
) {
}
