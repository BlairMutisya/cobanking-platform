package com.cobanking.account.service;

import com.cobanking.account.domain.Account;
import com.cobanking.account.client.AuditClient;
import com.cobanking.account.dto.AccountResponse;
import com.cobanking.account.dto.OpenAccountRequest;
import com.cobanking.account.repository.AccountRepository;
import com.cobanking.common.exception.ResourceNotFoundException;
import java.util.UUID;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.transaction.support.TransactionSynchronization;
import org.springframework.transaction.support.TransactionSynchronizationManager;

@Service
public class AccountService {
    private final AccountRepository accountRepository;
    private final AccountNumberGenerator accountNumberGenerator;
    private final AuditClient auditClient;

    public AccountService(
            AccountRepository accountRepository,
            AccountNumberGenerator accountNumberGenerator,
            AuditClient auditClient) {
        this.accountRepository = accountRepository;
        this.accountNumberGenerator = accountNumberGenerator;
        this.auditClient = auditClient;
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
        recordAccountOpenedAfterCommit(savedAccount);

        return toResponse(savedAccount);
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

    private void recordAccountOpenedAfterCommit(Account account) {
        if (!TransactionSynchronizationManager.isSynchronizationActive()) {
            auditClient.recordAccountOpened(account);
            return;
        }

        TransactionSynchronizationManager.registerSynchronization(new TransactionSynchronization() {
            @Override
            public void afterCommit() {
                auditClient.recordAccountOpened(account);
            }
        });
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
