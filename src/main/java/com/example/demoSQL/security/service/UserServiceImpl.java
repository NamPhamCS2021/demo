package com.example.demoSQL.security.service;

import com.example.demoSQL.security.entity.User;
import com.example.demoSQL.security.repository.UserRepository;
import org.springframework.stereotype.Service;

@Service
public class UserServiceImpl implements UserService{

    private final UserRepository userRepository;
    public UserServiceImpl(UserRepository userRepository) {
        this.userRepository = userRepository;
    }

    @Override
    public boolean existsByUsername(String username) {
        return userRepository.existsByUsername(username);
    }
    @Override
    public void save(User user) {
        userRepository.save(user);
    }
}
