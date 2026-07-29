package com.cobanking.ledger.outbox;

import java.util.List;
import java.util.UUID;
import org.springframework.data.jpa.repository.JpaRepository;

public interface AuditOutboxRepository extends JpaRepository<AuditOutboxEvent, UUID> {
    List<AuditOutboxEvent> findTop20ByStatusOrderByCreatedAtAsc(String status);
}
