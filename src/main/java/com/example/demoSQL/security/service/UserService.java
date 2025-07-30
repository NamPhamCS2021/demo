package com.example.demoSQL.security.service;

import com.example.demoSQL.security.entity.User;


public interface UserService {

    boolean existsByUsername(String username);
    void save(User user);
}
