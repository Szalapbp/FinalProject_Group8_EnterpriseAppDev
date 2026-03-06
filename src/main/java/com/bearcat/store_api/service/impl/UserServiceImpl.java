
package com.bearcat.store_api.service.impl;

import com.bearcat.store_api.dao.UserDao;
import com.bearcat.store_api.entities.User;
import com.bearcat.store_api.service.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import java.time.Instant;
import java.util.Optional;
import java.util.UUID;

@Service
public class UserServiceImpl implements UserService {

    @Autowired
    private final UserDao userDao;


    public UserServiceImpl(UserDao userDao) {
        this.userDao = userDao;
    }

    @Override
    public User registerUser(String email, String fullName, String password) {
        if (userDao.existsByEmail(email)) {
            throw new RuntimeException("Email already exists");
        }
        User user = new User();
        user.setEmail(email);
        user.setFullName(fullName);
        user.setPasswordHash(password);
        user.setCreatedAt(Instant.now());
        return userDao.save(user);
    }

    @Override
    public Optional<User> getUserById(UUID id) {
        return userDao.findById(id);
    }

    @Override
    public Optional<User> getUserByEmail(String email) {
        return userDao.findByEmail(email);
    }

    @Override
    public boolean authenticateUser(String email, String password) {
        return userDao.findByEmail(email)
                .map(user -> user.getPasswordHash().equals(password))
                .orElse(false);
    }
}
