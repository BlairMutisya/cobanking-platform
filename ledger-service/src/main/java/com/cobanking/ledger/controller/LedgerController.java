package com.cobanking.ledger.controller;

import com.cobanking.common.api.BaseApiResponse;
import com.cobanking.common.api.ServiceInfo;
import com.cobanking.common.security.RequestSecurity;
import com.cobanking.common.security.SecurityHeaders;
import com.cobanking.ledger.dto.response.LedgerBatchResponse;
import com.cobanking.ledger.dto.request.PostTransferLedgerRequest;
import com.cobanking.ledger.service.LedgerService;
import io.swagger.v3.oas.annotations.Operation;
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
@RequestMapping("/ledger")
@Tag(name = "Ledger", description = "Double-entry ledger endpoints")
public class LedgerController {
    private final LedgerService ledgerService;

    public LedgerController(LedgerService ledgerService) {
        this.ledgerService = ledgerService;
    }

    @GetMapping("/health")
    @Operation(summary = "Check ledger service health")
    public BaseApiResponse<ServiceInfo> health() {
        return BaseApiResponse.success("Ledger service is ready",
                new ServiceInfo("ledger-service", "UP", "Double-entry ledger foundation"));
    }

    @PostMapping("/transfers")
    @Operation(summary = "Post transfer ledger entries", description = "Creates a balanced debit and credit ledger batch for a transfer")
    public BaseApiResponse<LedgerBatchResponse> postTransfer(
            @RequestHeader(value = SecurityHeaders.AUTHENTICATED_TENANT_ID, required = false) String authenticatedTenantId,
            @Valid @RequestBody PostTransferLedgerRequest request) {
        RequestSecurity.requireTenantMatch(request.tenantId(), authenticatedTenantId);
        return BaseApiResponse.success("Ledger batch posted", ledgerService.postTransfer(request));
    }

    @GetMapping("/batches/{batchId}")
    @Operation(summary = "Get a ledger batch by UUID")
    public BaseApiResponse<LedgerBatchResponse> getBatch(
            @RequestHeader(value = SecurityHeaders.AUTHENTICATED_TENANT_ID, required = false) String authenticatedTenantId,
            @PathVariable UUID batchId,
            @RequestParam UUID tenantId) {
        RequestSecurity.requireTenantMatch(tenantId, authenticatedTenantId);
        return BaseApiResponse.success("Ledger batch found", ledgerService.getBatch(tenantId, batchId));
    }
}
