package com.cobanking.ledger.service;

import com.cobanking.common.exception.BusinessException;
import com.cobanking.common.exception.ResourceNotFoundException;
import com.cobanking.ledger.domain.EntryType;
import com.cobanking.ledger.domain.LedgerBatch;
import com.cobanking.ledger.domain.LedgerEntry;
import com.cobanking.ledger.dto.LedgerBatchResponse;
import com.cobanking.ledger.dto.LedgerEntryResponse;
import com.cobanking.ledger.dto.PostTransferLedgerRequest;
import com.cobanking.ledger.repository.LedgerBatchRepository;
import java.math.BigDecimal;
import java.util.UUID;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
public class LedgerService {
    private final LedgerBatchRepository ledgerBatchRepository;

    public LedgerService(LedgerBatchRepository ledgerBatchRepository) {
        this.ledgerBatchRepository = ledgerBatchRepository;
    }

    @Transactional
    public LedgerBatchResponse postTransfer(PostTransferLedgerRequest request) {
        return ledgerBatchRepository.findByTenantIdAndReferenceId(request.tenantId(), request.referenceId())
                .map(this::toResponse)
                .orElseGet(() -> createTransferBatch(request));
    }

    @Transactional(readOnly = true)
    public LedgerBatchResponse getBatch(UUID tenantId, UUID batchId) {
        LedgerBatch batch = ledgerBatchRepository.findByTenantIdAndId(tenantId, batchId)
                .orElseThrow(() -> new ResourceNotFoundException("LEDGER_BATCH_NOT_FOUND", "Ledger batch was not found"));

        return toResponse(batch);
    }

    private LedgerBatchResponse createTransferBatch(PostTransferLedgerRequest request) {
        if (request.debitAccountId().equals(request.creditAccountId())) {
            throw new BusinessException("LEDGER_SAME_ACCOUNT", "Debit and credit accounts must be different");
        }

        LedgerBatch batch = new LedgerBatch(
                request.tenantId(),
                request.referenceId(),
                "transaction-service",
                request.currency());

        batch.addEntry(new LedgerEntry(request.debitAccountId(), EntryType.DEBIT, request.amount()));
        batch.addEntry(new LedgerEntry(request.creditAccountId(), EntryType.CREDIT, request.amount()));
        ensureBalanced(batch);

        return toResponse(ledgerBatchRepository.save(batch));
    }

    private void ensureBalanced(LedgerBatch batch) {
        BigDecimal debitTotal = batch.getEntries().stream()
                .filter(entry -> entry.getEntryType() == EntryType.DEBIT)
                .map(LedgerEntry::getAmount)
                .reduce(BigDecimal.ZERO, BigDecimal::add);

        BigDecimal creditTotal = batch.getEntries().stream()
                .filter(entry -> entry.getEntryType() == EntryType.CREDIT)
                .map(LedgerEntry::getAmount)
                .reduce(BigDecimal.ZERO, BigDecimal::add);

        if (debitTotal.compareTo(creditTotal) != 0) {
            throw new BusinessException("LEDGER_NOT_BALANCED", "Ledger entries must balance before posting");
        }
    }

    private LedgerBatchResponse toResponse(LedgerBatch batch) {
        return new LedgerBatchResponse(
                batch.getId(),
                batch.getTenantId(),
                batch.getReferenceId(),
                batch.getSource(),
                batch.getCurrency(),
                batch.getPostedAt(),
                batch.getEntries().stream()
                        .map(entry -> new LedgerEntryResponse(
                                entry.getId(),
                                entry.getAccountId(),
                                entry.getEntryType(),
                                entry.getAmount()))
                        .toList());
    }
}
