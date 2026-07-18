package com.cobanking.ledger.repository;

import com.cobanking.ledger.entity.LedgerBatch;
import java.util.Optional;
import java.util.UUID;
import org.springframework.data.jpa.repository.JpaRepository;

public interface LedgerBatchRepository extends JpaRepository<LedgerBatch, UUID> {
    Optional<LedgerBatch> findByTenantIdAndReferenceId(UUID tenantId, UUID referenceId);

    Optional<LedgerBatch> findByTenantIdAndId(UUID tenantId, UUID id);
}
