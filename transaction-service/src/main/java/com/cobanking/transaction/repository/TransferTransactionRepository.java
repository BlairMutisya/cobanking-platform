package com.cobanking.transaction.repository;

import com.cobanking.transaction.domain.TransferTransaction;
import java.util.Optional;
import java.util.UUID;
import org.springframework.data.jpa.repository.JpaRepository;

public interface TransferTransactionRepository extends JpaRepository<TransferTransaction, UUID> {
    Optional<TransferTransaction> findByTenantIdAndIdempotencyKey(UUID tenantId, String idempotencyKey);

    Optional<TransferTransaction> findByTenantIdAndId(UUID tenantId, UUID id);
}
