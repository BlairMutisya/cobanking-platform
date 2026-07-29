package com.cobanking.account.service.impl;

import com.cobanking.account.entity.Account;
import com.cobanking.account.enums.AccountStatus;
import com.cobanking.account.dto.response.AccountResponse;
import com.cobanking.account.dto.response.AccountValidationResponse;
import com.cobanking.account.dto.request.OpenAccountRequest;
import com.cobanking.account.repository.AccountRepository;
import com.cobanking.account.outbox.AuditOutboxService;
import com.cobanking.account.service.AccountNumberGenerator;
import com.cobanking.account.service.AccountService;
import com.cobanking.common.exception.ResourceNotFoundException;
import java.util.UUID;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
public class AccountServiceImpl implements AccountService {
    private final AccountRepository accountRepository;
    private final AccountNumberGenerator accountNumberGenerator;
    private final AuditOutboxService auditOutboxService;

    public AccountServiceImpl(
            AccountRepository accountRepository,
            AccountNumberGenerator accountNumberGenerator,
            AuditOutboxService auditOutboxService) {
        this.accountRepository = accountRepository;
        this.accountNumberGenerator = accountNumberGenerator;
        this.auditOutboxService = auditOutboxService;
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

        Account savedAccount = accountRepository.save(account);
        auditOutboxService.recordAccountOpened(savedAccount);

        return toResponse(savedAccount);
    }

    @Transactional(readOnly = true)
    public AccountResponse getAccount(UUID tenantId, UUID accountId) {
        Account account = accountRepository.findByTenantIdAndId(tenantId, accountId)
                .orElseThrow(() -> new ResourceNotFoundException("ACCOUNT_NOT_FOUND", "Account was not found"));

        return toResponse(account);
    }

    @Transactional(readOnly = true)
    public AccountValidationResponse validateAccount(UUID tenantId, UUID accountId) {
        Account account = accountRepository.findByTenantIdAndId(tenantId, accountId)
                .orElseThrow(() -> new ResourceNotFoundException("ACCOUNT_NOT_FOUND", "Account was not found"));

        boolean active = account.getStatus() == AccountStatus.ACTIVE;
        return new AccountValidationResponse(
                account.getId(),
                account.getTenantId(),
                account.getCurrency(),
                account.getStatus(),
                active,
                active ? "Account is valid for transactions" : "Account is not active");
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
