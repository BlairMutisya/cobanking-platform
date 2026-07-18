package com.cobanking.audit.controller;

import com.cobanking.audit.dto.response.AuditEventResponse;
import com.cobanking.audit.dto.request.RecordAuditEventRequest;
import com.cobanking.audit.service.AuditService;
import com.cobanking.common.api.BaseApiResponse;
import com.cobanking.common.api.ServiceInfo;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import java.util.List;
import java.util.UUID;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/audit")
@Tag(name = "Audit", description = "Compliance audit trail endpoints")
public class AuditController {
    private final AuditService auditService;

    public AuditController(AuditService auditService) {
        this.auditService = auditService;
    }

    @GetMapping("/health")
    @Operation(summary = "Check audit service health")
    public BaseApiResponse<ServiceInfo> health() {
        return BaseApiResponse.success("Audit service is ready",
                new ServiceInfo("audit-service", "UP", "Compliance audit foundation"));
    }

    @PostMapping("/events")
    @Operation(summary = "Record an audit event")
    public BaseApiResponse<AuditEventResponse> recordEvent(@Valid @RequestBody RecordAuditEventRequest request) {
        return BaseApiResponse.success("Audit event recorded", auditService.recordEvent(request));
    }

    @GetMapping("/events")
    @Operation(summary = "Get recent audit events for a tenant")
    public BaseApiResponse<List<AuditEventResponse>> getRecentEvents(@RequestParam UUID tenantId) {
        return BaseApiResponse.success("Audit events found", auditService.getRecentEvents(tenantId));
    }
}
