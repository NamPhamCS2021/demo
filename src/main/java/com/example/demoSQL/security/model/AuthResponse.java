package com.example.demoSQL.security.model;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.util.Date;

@Setter
@Getter
@NoArgsConstructor
@AllArgsConstructor
public class AuthResponse {

    private String token;
    private String username;
    private String role;
    private Date expirationTime;
}
