package com.cobanking.account.controller;

import com.cobanking.account.dto.response.AccountResponse;
import com.cobanking.account.dto.response.AccountValidationResponse;
import com.cobanking.account.dto.request.OpenAccountRequest;
import com.cobanking.account.service.AccountService;
import com.cobanking.common.api.BaseApiResponse;
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
    public BaseApiResponse<ServiceInfo> health() {
        return BaseApiResponse.success("Account service is ready",
                new ServiceInfo("account-service", "UP", "Account lifecycle foundation"));
    }

    @PostMapping
    @Operation(summary = "Open an account", description = "Opens an account for a customer UUID and records an audit event after commit")
    public BaseApiResponse<AccountResponse> openAccount(@Valid @RequestBody OpenAccountRequest request) {
        return BaseApiResponse.success("Account opened", accountService.openAccount(request));
    }

    @GetMapping("/{accountId}")
    @Operation(summary = "Get an account by UUID")
    public BaseApiResponse<AccountResponse> getAccount(
            @PathVariable UUID accountId,
            @RequestParam UUID tenantId) {
        return BaseApiResponse.success("Account found", accountService.getAccount(tenantId, accountId));
    }

    @GetMapping("/{accountId}/validation")
    @Operation(summary = "Validate an account for transactions")
    public BaseApiResponse<AccountValidationResponse> validateAccount(
            @PathVariable UUID accountId,
            @RequestParam UUID tenantId) {
        return BaseApiResponse.success("Account validation completed",
                accountService.validateAccount(tenantId, accountId));
    }
}
