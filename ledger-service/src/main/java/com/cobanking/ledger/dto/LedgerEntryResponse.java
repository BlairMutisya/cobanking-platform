package com.cobanking.ledger.dto;

import com.cobanking.ledger.domain.EntryType;
import java.math.BigDecimal;
import java.util.UUID;

public record LedgerEntryResponse(
        UUID entryId,
        UUID accountId,
        EntryType entryType,
        BigDecimal amount
) {
}
