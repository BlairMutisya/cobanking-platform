package com.cobanking.account.service;

import com.cobanking.account.dto.request.OpenAccountRequest;
import com.cobanking.account.dto.response.AccountResponse;
import java.util.UUID;

public interface AccountService {
    AccountResponse openAccount(OpenAccountRequest request);

    AccountResponse getAccount(UUID tenantId, UUID accountId);
}
