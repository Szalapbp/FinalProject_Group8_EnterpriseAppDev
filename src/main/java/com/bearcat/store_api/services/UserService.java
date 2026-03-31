package com.bearcat.store_api.services;

import com.bearcat.store_api.entities.User;
import com.bearcat.store_api.repositories.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class UserService {
    private final UserRepository userRepository;

    public User registerUser(User user) {
        if (userRepository.findByEmail(user.getEmail()).isPresent()) {
            throw new RuntimeException("Email already exists");
        }

        return userRepository.save(user);
    }

    public User login(String email, String password) {
        User user = userRepository.findByEmail(email)
                .orElseThrow(() -> new RuntimeException("User not found"));

        // Simplified password check
        if (!user.getPasswordHash().equals(password)) {
            throw new RuntimeException("Invalid credentials");
        }
        return user;
    }
}