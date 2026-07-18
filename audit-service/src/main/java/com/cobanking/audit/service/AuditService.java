package com.cobanking.audit.service;

import com.cobanking.audit.dto.request.RecordAuditEventRequest;
import com.cobanking.audit.dto.response.AuditEventResponse;
import java.util.List;
import java.util.UUID;

public interface AuditService {
    AuditEventResponse recordEvent(RecordAuditEventRequest request);

    List<AuditEventResponse> getRecentEvents(UUID tenantId);
}
