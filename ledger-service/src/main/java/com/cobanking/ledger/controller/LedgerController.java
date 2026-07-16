package com.cobanking.ledger.controller;

import com.cobanking.common.api.ApiResponse;
import com.cobanking.common.api.ServiceInfo;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/ledger")
public class LedgerController {
    @GetMapping("/health")
    public ApiResponse<ServiceInfo> health() {
        return ApiResponse.ok("Ledger service is ready",
                new ServiceInfo("ledger-service", "UP", "Double-entry ledger foundation"));
    }
}
