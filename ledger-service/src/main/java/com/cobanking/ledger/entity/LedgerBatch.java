package com.cobanking.ledger.entity;

import jakarta.persistence.CascadeType;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.OneToMany;
import jakarta.persistence.PrePersist;
import jakarta.persistence.Table;
import jakarta.persistence.UniqueConstraint;
import java.time.Instant;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

@Entity
@Table(name = "ledger_batches", uniqueConstraints = {
        @UniqueConstraint(name = "uk_ledger_tenant_reference", columnNames = {"tenant_id", "reference_id"})
})
public class LedgerBatch {
    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    private UUID id;

    @Column(name = "tenant_id", nullable = false)
    private UUID tenantId;

    @Column(name = "reference_id", nullable = false)
    private UUID referenceId;

    @Column(nullable = false, length = 80)
    private String source;

    @Column(nullable = false, length = 3)
    private String currency;

    @Column(name = "posted_at", nullable = false, updatable = false)
    private Instant postedAt;

    @OneToMany(mappedBy = "batch", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<LedgerEntry> entries = new ArrayList<>();

    protected LedgerBatch() {
    }

    public LedgerBatch(UUID tenantId, UUID referenceId, String source, String currency) {
        this.tenantId = tenantId;
        this.referenceId = referenceId;
        this.source = source;
        this.currency = currency.toUpperCase();
    }

    @PrePersist
    void onCreate() {
        postedAt = Instant.now();
    }

    public void addEntry(LedgerEntry entry) {
        entries.add(entry);
        entry.assignTo(this);
    }

    public UUID getId() {
        return id;
    }

    public UUID getTenantId() {
        return tenantId;
    }

    public UUID getReferenceId() {
        return referenceId;
    }

    public String getSource() {
        return source;
    }

    public String getCurrency() {
        return currency;
    }

    public Instant getPostedAt() {
        return postedAt;
    }

    public List<LedgerEntry> getEntries() {
        return List.copyOf(entries);
    }
}
