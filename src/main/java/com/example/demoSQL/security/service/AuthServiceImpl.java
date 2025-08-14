package com.example.demoSQL.security.service;

import com.example.demoSQL.dto.ApiResponse;
import com.example.demoSQL.enums.ReturnMessage;
import com.example.demoSQL.enums.UserRole;
import com.example.demoSQL.security.entity.User;
import com.example.demoSQL.security.model.AuthRequest;
import com.example.demoSQL.security.model.AuthResponse;
import com.example.demoSQL.security.model.SignUpResponse;
import com.example.demoSQL.security.model.UserDetailsImpl;
import com.example.demoSQL.security.repository.UserRepository;
import com.example.demoSQL.security.utils.JwtUtil;
import jakarta.servlet.http.Cookie;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import java.util.Date;
import java.util.List;
import java.util.stream.Collectors;
@Service
public class AuthServiceImpl implements AuthService {

    @Autowired
    private UserService userService;

    @Autowired
    private PasswordEncoder passwordEncoder;

    @Autowired
    private JwtUtil jwtUtil;

    @Autowired
    private AuthenticationManager authenticationManager;

    @Autowired
    private UserRepository userRepository;

    public AuthServiceImpl(UserService userService, PasswordEncoder passwordEncoder, AuthenticationManager authenticationManager, UserRepository userRepository) {
        this.userService = userService;
        this.passwordEncoder = passwordEncoder;
        this.authenticationManager = authenticationManager;
        this.userRepository = userRepository;
    }

    @Override
    public ApiResponse<Object> login(AuthRequest request, HttpServletResponse response) {
        Authentication authentication = authenticationManager.authenticate(
                new UsernamePasswordAuthenticationToken(request.getUsername(), request.getPassword()));

        SecurityContextHolder.getContext().setAuthentication(authentication);
        String jwt = jwtUtil.generateJwtToken(authentication);

        UserDetailsImpl userDetails = (UserDetailsImpl) authentication.getPrincipal();
        AuthResponse authResponse = new AuthResponse();
        authResponse.setToken(jwt);
        authResponse.setUsername(userDetails.getUsername());
        authResponse.setExpirationTime(new Date(System.currentTimeMillis() + 720000));

        // Set cookie here
        Cookie cookie = new Cookie("jwt", jwt);
        cookie.setHttpOnly(true);
        cookie.setSecure(true);
        cookie.setPath("/");
        cookie.setMaxAge(7 * 24 * 60 * 60);
        response.addCookie(cookie);

        return new ApiResponse<>(authResponse, ReturnMessage.SUCCESS.getCode(), ReturnMessage.SUCCESS.getMessage());
    }


    @Override
    public ApiResponse<Object> adminRegister(AuthRequest authRequest) {
        User user = new User();
        user.setUsername(authRequest.getUsername());
        user.setPassword(passwordEncoder.encode(authRequest.getPassword()));
        user.setRole(UserRole.ADMIN);

        SignUpResponse response = new SignUpResponse();
        response.setRole(user.getRole());
        response.setUsername(user.getUsername());
        userRepository.save(user);

        return new ApiResponse<>(response, ReturnMessage.SUCCESS.getCode(), ReturnMessage.SUCCESS.getMessage());
    }
}
