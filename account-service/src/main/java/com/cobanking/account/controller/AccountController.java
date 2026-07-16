package com.cobanking.account.controller;

import com.cobanking.common.api.ApiResponse;
import com.cobanking.common.api.ServiceInfo;
import jakarta.validation.Valid;
import jakarta.validation.constraints.NotBlank;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/accounts")
public class AccountController {
    @GetMapping("/health")
    public ApiResponse<ServiceInfo> health() {
        return ApiResponse.ok("Account service is ready",
                new ServiceInfo("account-service", "UP", "Account lifecycle foundation"));
    }

    @PostMapping
    public ApiResponse<AccountResponse> openAccount(@Valid @RequestBody OpenAccountRequest request) {
        return ApiResponse.ok("Account opening placeholder accepted",
                new AccountResponse("account-id-will-be-generated-later", request.customerId(), "ACTIVE"));
    }

    public record OpenAccountRequest(@NotBlank String customerId, @NotBlank String accountType) {
    }

    public record AccountResponse(String accountId, String customerId, String status) {
    }
}
