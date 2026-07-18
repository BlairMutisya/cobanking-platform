package com.cobanking.transaction.service.impl;

import com.cobanking.common.exception.BusinessException;
import com.cobanking.common.exception.ResourceNotFoundException;
import com.cobanking.transaction.client.LedgerClient;
import com.cobanking.transaction.entity.TransferTransaction;
import com.cobanking.transaction.dto.request.TransferRequest;
import com.cobanking.transaction.dto.response.TransferResponse;
import com.cobanking.transaction.repository.TransferTransactionRepository;
import com.cobanking.transaction.service.TransactionService;
import java.util.UUID;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.client.RestClientException;

@Service
public class TransactionServiceImpl implements TransactionService {
    private final TransferTransactionRepository transferTransactionRepository;
    private final LedgerClient ledgerClient;

    public TransactionServiceImpl(TransferTransactionRepository transferTransactionRepository, LedgerClient ledgerClient) {
        this.transferTransactionRepository = transferTransactionRepository;
        this.ledgerClient = ledgerClient;
    }

    @Transactional
    public TransferResponse requestTransfer(String idempotencyKey, TransferRequest request) {
        return transferTransactionRepository.findByTenantIdAndIdempotencyKey(request.tenantId(), idempotencyKey)
                .map(this::toResponse)
                .orElseGet(() -> createAndPostTransfer(idempotencyKey, request));
    }

    @Transactional(readOnly = true)
    public TransferResponse getTransaction(UUID tenantId, UUID transactionId) {
        TransferTransaction transaction = transferTransactionRepository.findByTenantIdAndId(tenantId, transactionId)
                .orElseThrow(() -> new ResourceNotFoundException("TRANSACTION_NOT_FOUND", "Transaction was not found"));

        return toResponse(transaction);
    }

    private TransferResponse createAndPostTransfer(String idempotencyKey, TransferRequest request) {
        if (request.debitAccountId().equals(request.creditAccountId())) {
            throw new BusinessException("TRANSFER_SAME_ACCOUNT", "Debit and credit accounts must be different");
        }

        TransferTransaction transaction = new TransferTransaction(
                request.tenantId(),
                idempotencyKey,
                request.debitAccountId(),
                request.creditAccountId(),
                request.amount(),
                request.currency());

        TransferTransaction savedTransaction = transferTransactionRepository.saveAndFlush(transaction);

        try {
            LedgerClient.LedgerBatchResponse ledgerBatch = ledgerClient.postTransfer(savedTransaction);
            savedTransaction.markPosted(ledgerBatch.batchId());
        } catch (RestClientException exception) {
            savedTransaction.markFailed("Ledger posting failed");
        }

        return toResponse(savedTransaction);
    }

    private TransferResponse toResponse(TransferTransaction transaction) {
        return new TransferResponse(
                transaction.getId(),
                transaction.getTenantId(),
                transaction.getIdempotencyKey(),
                transaction.getDebitAccountId(),
                transaction.getCreditAccountId(),
                transaction.getAmount(),
                transaction.getCurrency(),
                transaction.getStatus(),
                transaction.getLedgerBatchId(),
                transaction.getFailureReason(),
                transaction.getCreatedAt());
    }
}
