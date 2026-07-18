package com.cobanking.transaction.service;

import com.cobanking.transaction.dto.request.TransferRequest;
import com.cobanking.transaction.dto.response.TransferResponse;
import java.util.UUID;

public interface TransactionService {
    TransferResponse requestTransfer(String idempotencyKey, TransferRequest request);

    TransferResponse getTransaction(UUID tenantId, UUID transactionId);
}
