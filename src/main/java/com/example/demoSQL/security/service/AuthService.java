package com.example.demoSQL.security.service;

import com.example.demoSQL.dto.ApiResponse;
import com.example.demoSQL.security.model.AuthRequest;
import com.example.demoSQL.security.model.AuthResponse;
import com.example.demoSQL.security.model.SignUpResponse;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;

public interface AuthService {
    ApiResponse<Object> login(AuthRequest authRequest, HttpServletResponse response);
    ApiResponse<Object> adminRegister(AuthRequest authRequest);
    ApiResponse<Object> getCurrentUser(Authentication authentication);
}
