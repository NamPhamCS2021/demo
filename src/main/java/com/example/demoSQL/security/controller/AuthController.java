package com.example.demoSQL.security.controller;

import com.example.demoSQL.dto.ApiResponse;
import com.example.demoSQL.security.entity.User;
import com.example.demoSQL.security.model.AuthRequest;
import com.example.demoSQL.security.model.AuthResponse;
import com.example.demoSQL.security.model.SignUpResponse;
import com.example.demoSQL.security.model.UserDTO;
import com.example.demoSQL.security.service.AuthService;
import com.example.demoSQL.security.service.UserService;
import jakarta.servlet.http.HttpServletResponse;
import lombok.NoArgsConstructor;
import lombok.RequiredArgsConstructor;
import org.checkerframework.checker.units.qual.A;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseCookie;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;

import java.util.Map;

@RequestMapping("/api/auth")
@RequiredArgsConstructor
@RestController
public class AuthController {

    private final AuthService authService;

    private final UserService userService;

    @PostMapping("/login")
    public ResponseEntity<ApiResponse<Object>> login(@RequestBody AuthRequest request, HttpServletResponse response) {
        return ResponseEntity.ok(authService.login(request, response));
    }



    @PostMapping("/aLogin")
    public ApiResponse<Object> adminLogin(@RequestBody AuthRequest request){
        ApiResponse<Object> response = authService.adminRegister(request);
        return response;
    }
    @PostMapping("/aSignup")
    public ApiResponse<Object> adminSignUp(@RequestBody AuthRequest request){
        ApiResponse<Object> response = authService.adminRegister(request);
        return response;
    }

    @GetMapping("/me")
    @PreAuthorize("hasRole('USER') or hasRole('ADMIN')")
    public ApiResponse<Object> getCurrentUser(Authentication auth) {
        ApiResponse<Object> user = userService.findByUsername(auth.getName());
        return user;
    }

    @GetMapping("/check")
    public ResponseEntity<String> checkAuth() {
        return ResponseEntity.ok("Authenticated");
    }


    @PostMapping("/logout")
    public ResponseEntity<?> logout(HttpServletResponse response) {
        // Overwrite the JWT cookie with an expired one
        ResponseCookie expiredCookie = ResponseCookie.from("jwt", "")
                .path("/")            // must match the original cookie path
                .httpOnly(true)       // same as your login cookie
                .secure(false)        // set true if using HTTPS
                .sameSite("Lax")      // or "Strict" depending on your setup
                .maxAge(0)            // delete immediately
                .build();
        response.addHeader(HttpHeaders.SET_COOKIE, expiredCookie.toString());

        return ResponseEntity.ok(Map.of(
                "resultCode", "00",
                "resultMessage", "Logged out successfully"
        ));
    }

}
 