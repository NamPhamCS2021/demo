package com.example.demoSQL.security.controller;

import com.example.demoSQL.security.model.AuthRequest;
import com.example.demoSQL.security.model.AuthResponse;
import com.example.demoSQL.security.service.AuthService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;

@RestController
public class AuthController {

    @Autowired
    private AuthService authService;

    @PostMapping("/api/login")
    public ResponseEntity<AuthResponse> login(@RequestBody AuthRequest request){
        ResponseEntity<AuthResponse> response = authService.login(request);
        return response;
    }
}
