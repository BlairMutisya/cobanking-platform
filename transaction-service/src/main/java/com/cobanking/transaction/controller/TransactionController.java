package com.cobanking.transaction.controller;

import com.cobanking.common.api.ApiResponse;
import com.cobanking.common.api.ServiceInfo;
import jakarta.validation.Valid;
import jakarta.validation.constraints.DecimalMin;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import java.math.BigDecimal;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestHeader;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/transactions")
public class TransactionController {
    @GetMapping("/health")
    public ApiResponse<ServiceInfo> health() {
        return ApiResponse.ok("Transaction service is ready",
                new ServiceInfo("transaction-service", "UP", "Transaction orchestration foundation"));
    }

    @PostMapping("/transfers")
    public ApiResponse<TransferResponse> requestTransfer(
            @RequestHeader("Idempotency-Key") String idempotencyKey,
            @Valid @RequestBody TransferRequest request) {
        return ApiResponse.ok("Transfer placeholder accepted",
                new TransferResponse("transfer-id-will-be-generated-later", idempotencyKey, "RECEIVED"));
    }

    public record TransferRequest(
            @NotBlank String debitAccountId,
            @NotBlank String creditAccountId,
            @NotNull @DecimalMin("0.01") BigDecimal amount,
            @NotBlank String currency
    ) {
    }

    public record TransferResponse(String transferId, String idempotencyKey, String status) {
    }
}
