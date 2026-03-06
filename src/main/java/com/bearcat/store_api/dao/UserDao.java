package com.bearcat.store_api.dao;

import com.bearcat.store_api.entities.User;
import org.springframework.context.annotation.Bean;
import org.springframework.stereotype.Component;

import java.util.Optional;
import java.util.UUID;


public interface UserDao {
    Optional<User> findByEmail(String email);
    Optional<User> findById(UUID id);
    User save(User user);
    boolean existsByEmail(String email);
    void deleteById(UUID id);
}