package com.cobanking.auth.controller;

import com.cobanking.auth.dto.request.LoginRequest;
import com.cobanking.auth.dto.response.LoginResponse;
import com.cobanking.auth.service.AuthService;
import com.cobanking.common.api.BaseApiResponse;
import com.cobanking.common.api.ServiceInfo;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/auth")
@Tag(name = "Authentication", description = "Identity and access endpoints")
public class AuthController {
    private final AuthService authService;

    public AuthController(AuthService authService) {
        this.authService = authService;
    }

    @GetMapping("/health")
    @Operation(summary = "Check auth service health")
    public BaseApiResponse<ServiceInfo> health() {
        return BaseApiResponse.success("Auth service is ready",
                new ServiceInfo("auth-service", "UP", "Identity and access foundation"));
    }

    @PostMapping("/login")
    @Operation(summary = "Login and receive a JWT", description = "Uses demo users for the learning foundation. Later this moves to database-backed users.")
    public BaseApiResponse<LoginResponse> login(@Valid @RequestBody LoginRequest request) {
        return BaseApiResponse.success("Login successful", authService.login(request));
    }
}
