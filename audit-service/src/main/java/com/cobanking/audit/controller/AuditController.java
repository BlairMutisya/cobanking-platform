package com.cobanking.audit.controller;

import com.cobanking.common.api.ApiResponse;
import com.cobanking.common.api.ServiceInfo;
import jakarta.validation.Valid;
import jakarta.validation.constraints.NotBlank;
import java.time.Instant;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/audit")
public class AuditController {
    @GetMapping("/health")
    public ApiResponse<ServiceInfo> health() {
        return ApiResponse.ok("Audit service is ready",
                new ServiceInfo("audit-service", "UP", "Compliance audit foundation"));
    }

    @PostMapping("/events")
    public ApiResponse<AuditEventResponse> recordEvent(@Valid @RequestBody AuditEventRequest request) {
        return ApiResponse.ok("Audit event placeholder accepted",
                new AuditEventResponse("audit-event-id-will-be-generated-later", request.action(), Instant.now()));
    }

    public record AuditEventRequest(@NotBlank String actor, @NotBlank String action, @NotBlank String resource) {
    }

    public record AuditEventResponse(String eventId, String action, Instant recordedAt) {
    }
}
