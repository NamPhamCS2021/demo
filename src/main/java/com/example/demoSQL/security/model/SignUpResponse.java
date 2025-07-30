package com.example.demoSQL.security.model;

import com.example.demoSQL.enums.UserRole;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Setter
@Getter
@NoArgsConstructor
@AllArgsConstructor
public class SignUpResponse {
    private String username;
    private UserRole role;
}
