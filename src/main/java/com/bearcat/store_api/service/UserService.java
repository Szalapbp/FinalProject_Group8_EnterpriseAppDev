package com.bearcat.store_api.service;
import com.bearcat.store_api.entities.User;
import java.util.Optional;
import java.util.UUID;

public interface UserService {
    User registerUser(String email, String fullName, String password);
    Optional<User> getUserById(UUID id);
    Optional<User> getUserByEmail(String email);
    boolean authenticateUser(String email, String password);
}
