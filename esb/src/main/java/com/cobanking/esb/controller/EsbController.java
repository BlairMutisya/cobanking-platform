package com.cobanking.esb.controller;

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
import reactor.core.publisher.Mono;

@RestController
@RequestMapping("/esb")
public class EsbController {
    @GetMapping("/health")
    public Mono<ApiResponse<ServiceInfo>> health() {
        return Mono.just(ApiResponse.ok("ESB is ready",
                new ServiceInfo("esb", "UP", "Reactive integration foundation")));
    }

    @PostMapping("/routes/{routeKey}/dispatch")
    public Mono<ApiResponse<DispatchResponse>> dispatch(
            @org.springframework.web.bind.annotation.PathVariable String routeKey,
            @Valid @RequestBody DispatchRequest request) {
        var response = new DispatchResponse(routeKey, request.correlationId(), "ROUTE_ACCEPTED", Instant.now());
        return Mono.just(ApiResponse.ok("Dispatch placeholder accepted", response));
    }

    public record DispatchRequest(@NotBlank String correlationId, @NotBlank String payload) {
    }

    public record DispatchResponse(String routeKey, String correlationId, String status, Instant acceptedAt) {
    }
}
