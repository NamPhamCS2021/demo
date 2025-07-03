package com.example.demoSQL.security.service;

import com.example.demoSQL.security.model.AuthRequest;
import com.example.demoSQL.security.model.AuthResponse;
import com.example.demoSQL.security.model.SignUpResponse;
import org.springframework.http.ResponseEntity;

public interface AuthService {
    ResponseEntity<AuthResponse> login(AuthRequest authRequest);
    ResponseEntity<SignUpResponse> adminRegister(AuthRequest authRequest);
}
