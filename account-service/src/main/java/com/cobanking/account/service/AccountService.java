package com.cobanking.account.service;

import com.cobanking.account.domain.Account;
import com.cobanking.account.dto.AccountResponse;
import com.cobanking.account.dto.OpenAccountRequest;
import com.cobanking.account.repository.AccountRepository;
import com.cobanking.common.exception.ResourceNotFoundException;
import java.util.UUID;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
public class AccountService {
    private final AccountRepository accountRepository;
    private final AccountNumberGenerator accountNumberGenerator;

    public AccountService(AccountRepository accountRepository, AccountNumberGenerator accountNumberGenerator) {
        this.accountRepository = accountRepository;
        this.accountNumberGenerator = accountNumberGenerator;
    }

    @Transactional
    public AccountResponse openAccount(OpenAccountRequest request) {
        String accountNumber = nextUniqueAccountNumber();
        Account account = new Account(
                request.tenantId(),
                request.customerId(),
                accountNumber,
                request.accountType(),
                request.currency());

        return toResponse(accountRepository.save(account));
    }

    @Transactional(readOnly = true)
    public AccountResponse getAccount(UUID tenantId, UUID accountId) {
        Account account = accountRepository.findByTenantIdAndId(tenantId, accountId)
                .orElseThrow(() -> new ResourceNotFoundException("ACCOUNT_NOT_FOUND", "Account was not found"));

        return toResponse(account);
    }

    private String nextUniqueAccountNumber() {
        String accountNumber;
        do {
            accountNumber = accountNumberGenerator.generate();
        } while (accountRepository.existsByAccountNumber(accountNumber));
        return accountNumber;
    }

    private AccountResponse toResponse(Account account) {
        return new AccountResponse(
                account.getId(),
                account.getTenantId(),
                account.getCustomerId(),
                account.getAccountNumber(),
                account.getAccountType(),
                account.getCurrency(),
                account.getStatus(),
                account.getCreatedAt());
    }
}
