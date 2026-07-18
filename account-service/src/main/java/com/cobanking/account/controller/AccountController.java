package com.cobanking.account.controller;

import com.cobanking.account.dto.AccountResponse;
import com.cobanking.account.dto.OpenAccountRequest;
import com.cobanking.account.service.AccountService;
import com.cobanking.common.api.ApiResponse;
import com.cobanking.common.api.ServiceInfo;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import java.util.UUID;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/accounts")
@Tag(name = "Accounts", description = "Bank account lifecycle endpoints")
public class AccountController {
    private final AccountService accountService;

    public AccountController(AccountService accountService) {
        this.accountService = accountService;
    }

    @GetMapping("/health")
    @Operation(summary = "Check account service health")
    public ApiResponse<ServiceInfo> health() {
        return ApiResponse.ok("Account service is ready",
                new ServiceInfo("account-service", "UP", "Account lifecycle foundation"));
    }

    @PostMapping
    @Operation(summary = "Open an account", description = "Opens an account for a customer UUID and records an audit event after commit")
    public ApiResponse<AccountResponse> openAccount(@Valid @RequestBody OpenAccountRequest request) {
        return ApiResponse.ok("Account opened", accountService.openAccount(request));
    }

    @GetMapping("/{accountId}")
    @Operation(summary = "Get an account by UUID")
    public ApiResponse<AccountResponse> getAccount(
            @PathVariable UUID accountId,
            @RequestParam UUID tenantId) {
        return ApiResponse.ok("Account found", accountService.getAccount(tenantId, accountId));
    }
}
