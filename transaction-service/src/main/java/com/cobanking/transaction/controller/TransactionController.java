package com.cobanking.transaction.controller;

import com.cobanking.common.api.ApiResponse;
import com.cobanking.common.api.ServiceInfo;
import com.cobanking.transaction.dto.TransferRequest;
import com.cobanking.transaction.dto.TransferResponse;
import com.cobanking.transaction.service.TransactionService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import java.util.UUID;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestHeader;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/transactions")
@Tag(name = "Transactions", description = "Money movement orchestration endpoints")
public class TransactionController {
    private final TransactionService transactionService;

    public TransactionController(TransactionService transactionService) {
        this.transactionService = transactionService;
    }

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
        return ApiResponse.ok("Transfer processed", transactionService.requestTransfer(idempotencyKey, request));
    }

    @GetMapping("/{transactionId}")
    @Operation(summary = "Get a transaction by UUID")
    public ApiResponse<TransferResponse> getTransaction(
            @PathVariable UUID transactionId,
            @RequestParam UUID tenantId) {
        return ApiResponse.ok("Transaction found", transactionService.getTransaction(tenantId, transactionId));
    }
}
