package com.cobanking.auth.service;

import com.cobanking.auth.dto.request.LoginRequest;
import com.cobanking.auth.dto.response.LoginResponse;

public interface AuthService {
    LoginResponse login(LoginRequest request);
}
