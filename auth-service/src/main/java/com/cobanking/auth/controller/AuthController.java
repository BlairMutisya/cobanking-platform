package com.cobanking.auth.controller;

import com.cobanking.common.api.ApiResponse;
import com.cobanking.common.api.ServiceInfo;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import jakarta.validation.constraints.NotBlank;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/auth")
@Tag(name = "Authentication", description = "Identity and access endpoints")
public class AuthController {
    @GetMapping("/health")
    @Operation(summary = "Check auth service health")
    public ApiResponse<ServiceInfo> health() {
        return ApiResponse.ok("Auth service is ready",
                new ServiceInfo("auth-service", "UP", "Identity and access foundation"));
    }

    @PostMapping("/login")
    @Operation(summary = "Login placeholder", description = "Temporary login endpoint until JWT security is implemented")
    public ApiResponse<LoginResponse> login(@Valid @RequestBody LoginRequest request) {
        return ApiResponse.ok("Login placeholder accepted",
                new LoginResponse(request.username(), "jwt-token-will-be-added-in-security-phase"));
    }

    public record LoginRequest(@NotBlank String username, @NotBlank String password) {
    }

    public record LoginResponse(String username, String accessToken) {
    }
}
