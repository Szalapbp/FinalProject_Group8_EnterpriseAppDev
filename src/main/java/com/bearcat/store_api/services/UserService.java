package com.bearcat.store_api.services;

import com.bearcat.store_api.entities.User;
import com.bearcat.store_api.repositories.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.stereotype.Service;

import java.util.Optional;
import java.util.UUID;

@Service
@RequiredArgsConstructor
public class UserService {


    private final UserRepository userRepository;


    public User saveUser(User user) {


        if (user.getEmail() == null || user.getEmail().isEmpty()) {
            throw new RuntimeException("Email cannot be empty");
        }

        return userRepository.save(user);
    }


    public Optional<User> getUserByEmail(String email) {

        return userRepository.findByEmail(email);
    }

    public User getCurrentUser() {
        Object principal = SecurityContextHolder.getContext()
                .getAuthentication().getPrincipal();

        String email;
        if (principal instanceof UserDetails) {
            email = ((UserDetails) principal).getUsername();
        } else {
            email = principal.toString();
        }

        return userRepository.findByEmail(email)
                .orElseThrow(() -> new RuntimeException("User not found"));
    }



    // Add to UserService — needed by CartController and OrderController
    public User getUserById(UUID id) {
        return userRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("User not found"));
    }


}