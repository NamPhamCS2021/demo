package com.example.demoSQL.security.model;

import com.example.demoSQL.enums.UserRole;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class UserDTO {
    private String username;
    private UserRole role;
    private LocalDateTime createdDate;
}
