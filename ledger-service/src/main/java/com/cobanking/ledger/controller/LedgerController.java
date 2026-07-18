package com.cobanking.ledger.controller;

import com.cobanking.common.api.ApiResponse;
import com.cobanking.common.api.ServiceInfo;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/ledger")
@Tag(name = "Ledger", description = "Double-entry ledger endpoints")
public class LedgerController {
    @GetMapping("/health")
    @Operation(summary = "Check ledger service health")
    public ApiResponse<ServiceInfo> health() {
        return ApiResponse.ok("Ledger service is ready",
                new ServiceInfo("ledger-service", "UP", "Double-entry ledger foundation"));
    }
}
