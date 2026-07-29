package com.cobanking.ledger.outbox;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.PrePersist;
import jakarta.persistence.Table;
import java.time.Instant;
import java.util.UUID;

@Entity
@Table(name = "audit_outbox_events")
public class AuditOutboxEvent {
    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    private UUID id;

    @Column(name = "tenant_id", nullable = false)
    private UUID tenantId;

    @Column(nullable = false, length = 120)
    private String actor;

    @Column(nullable = false, length = 120)
    private String action;

    @Column(nullable = false, length = 120)
    private String resource;

    @Column(name = "resource_id")
    private UUID resourceId;

    @Column(columnDefinition = "text")
    private String metadata;

    @Column(nullable = false, length = 30)
    private String status;

    @Column(nullable = false)
    private int attempts;

    @Column(name = "last_error", length = 500)
    private String lastError;

    @Column(name = "created_at", nullable = false, updatable = false)
    private Instant createdAt;

    @Column(name = "published_at")
    private Instant publishedAt;

    protected AuditOutboxEvent() {
    }

    public AuditOutboxEvent(UUID tenantId, String actor, String action, String resource, UUID resourceId, String metadata) {
        this.tenantId = tenantId;
        this.actor = actor;
        this.action = action;
        this.resource = resource;
        this.resourceId = resourceId;
        this.metadata = metadata;
        this.status = "PENDING";
    }

    @PrePersist
    void onCreate() {
        createdAt = Instant.now();
    }

    public void markPublished() {
        status = "PUBLISHED";
        publishedAt = Instant.now();
        lastError = null;
    }

    public void markFailed(String error) {
        attempts++;
        status = attempts >= 5 ? "FAILED" : "PENDING";
        lastError = error == null ? "Unknown publishing error" : error.substring(0, Math.min(error.length(), 500));
    }

    public UUID getTenantId() { return tenantId; }
    public String getActor() { return actor; }
    public String getAction() { return action; }
    public String getResource() { return resource; }
    public UUID getResourceId() { return resourceId; }
    public String getMetadata() { return metadata; }
}
