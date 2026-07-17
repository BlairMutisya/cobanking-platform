package com.cobanking.audit.service;

import com.cobanking.audit.domain.AuditEvent;
import com.cobanking.audit.dto.AuditEventResponse;
import com.cobanking.audit.dto.RecordAuditEventRequest;
import com.cobanking.audit.repository.AuditEventRepository;
import java.util.List;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
public class AuditService {
    private final AuditEventRepository auditEventRepository;

    public AuditService(AuditEventRepository auditEventRepository) {
        this.auditEventRepository = auditEventRepository;
    }

    @Transactional
    public AuditEventResponse recordEvent(RecordAuditEventRequest request) {
        AuditEvent event = new AuditEvent(
                request.tenantId(),
                request.actor(),
                request.action(),
                request.resource(),
                request.resourceId(),
                request.metadata());

        return toResponse(auditEventRepository.save(event));
    }

    @Transactional(readOnly = true)
    public List<AuditEventResponse> getRecentEvents(java.util.UUID tenantId) {
        return auditEventRepository.findTop50ByTenantIdOrderByRecordedAtDesc(tenantId)
                .stream()
                .map(this::toResponse)
                .toList();
    }

    private AuditEventResponse toResponse(AuditEvent event) {
        return new AuditEventResponse(
                event.getId(),
                event.getTenantId(),
                event.getActor(),
                event.getAction(),
                event.getResource(),
                event.getResourceId(),
                event.getMetadata(),
                event.getRecordedAt());
    }
}
