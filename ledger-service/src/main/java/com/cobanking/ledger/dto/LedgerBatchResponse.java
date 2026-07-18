package com.cobanking.ledger.dto;

import java.time.Instant;
import java.util.List;
import java.util.UUID;

public record LedgerBatchResponse(
        UUID batchId,
        UUID tenantId,
        UUID referenceId,
        String source,
        String currency,
        Instant postedAt,
        List<LedgerEntryResponse> entries
) {
}
