package com.cobanking.ledger.service;

import com.cobanking.ledger.dto.request.PostTransferLedgerRequest;
import com.cobanking.ledger.dto.response.LedgerBatchResponse;
import java.util.UUID;

public interface LedgerService {
    LedgerBatchResponse postTransfer(PostTransferLedgerRequest request);

    LedgerBatchResponse getBatch(UUID tenantId, UUID batchId);
}
