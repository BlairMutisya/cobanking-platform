package com.cobanking.ledger.dto.response;

import com.cobanking.ledger.enums.EntryType;
import java.math.BigDecimal;
import java.util.UUID;

public record LedgerEntryResponse(
        UUID entryId,
        UUID accountId,
        EntryType entryType,
        BigDecimal amount
) {
}
