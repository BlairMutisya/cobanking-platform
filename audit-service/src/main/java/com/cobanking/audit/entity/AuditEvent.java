package com.cobanking.audit.entity;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.Lob;
import jakarta.persistence.PrePersist;
import jakarta.persistence.Table;
import java.time.Instant;
import java.util.UUID;

@Entity
@Table(name = "audit_events")
public class AuditEvent {
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

    @Lob
    @Column(name = "metadata")
    private String metadata;

    @Column(name = "recorded_at", nullable = false, updatable = false)
    private Instant recordedAt;

    protected AuditEvent() {
    }

    public AuditEvent(UUID tenantId, String actor, String action, String resource, UUID resourceId, String metadata) {
        this.tenantId = tenantId;
        this.actor = actor;
        this.action = action;
        this.resource = resource;
        this.resourceId = resourceId;
        this.metadata = metadata;
    }

    @PrePersist
    void onCreate() {
        recordedAt = Instant.now();
    }

    public UUID getId() {
        return id;
    }

    public UUID getTenantId() {
        return tenantId;
    }

    public String getActor() {
        return actor;
    }

    public String getAction() {
        return action;
    }

    public String getResource() {
        return resource;
    }

    public UUID getResourceId() {
        return resourceId;
    }

    public String getMetadata() {
        return metadata;
    }

    public Instant getRecordedAt() {
        return recordedAt;
    }
}
