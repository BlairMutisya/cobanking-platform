package com.cobanking.transaction.controller;

import com.cobanking.common.api.ApiResponse;
import com.cobanking.common.api.ServiceInfo;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import jakarta.validation.constraints.DecimalMin;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import java.math.BigDecimal;
import java.util.UUID;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestHeader;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/transactions")
@Tag(name = "Transactions", description = "Money movement orchestration endpoints")
public class TransactionController {
    @GetMapping("/health")
    @Operation(summary = "Check transaction service health")
    public ApiResponse<ServiceInfo> health() {
        return ApiResponse.ok("Transaction service is ready",
                new ServiceInfo("transaction-service", "UP", "Transaction orchestration foundation"));
    }

    @PostMapping("/transfers")
    @Operation(summary = "Request a transfer", description = "Receives a transfer request using an idempotency key to prevent duplicate processing")
    public ApiResponse<TransferResponse> requestTransfer(
            @Parameter(description = "Unique request key used to avoid duplicate transfer processing")
            @RequestHeader("Idempotency-Key") String idempotencyKey,
            @Valid @RequestBody TransferRequest request) {
        return ApiResponse.ok("Transfer placeholder accepted",
                new TransferResponse("transfer-id-will-be-generated-later", idempotencyKey, "RECEIVED"));
    }

    public record TransferRequest(
            @NotNull UUID debitAccountId,
            @NotNull UUID creditAccountId,
            @NotNull @DecimalMin("0.01") BigDecimal amount,
            @NotBlank String currency
    ) {
    }

    public record TransferResponse(String transferId, String idempotencyKey, String status) {
    }
}
