package com.bearcat.store_api.dao.stub;

import com.bearcat.store_api.dao.UserDao;
import com.bearcat.store_api.entities.User;
import org.springframework.stereotype.Repository;
import java.time.Instant;
import java.util.*;

@Repository
public class UserDaoStub implements UserDao {
    private final Map<UUID, User> store = new HashMap<>();

    public UserDaoStub() {
        User user = new User();
        user.setId(UUID.randomUUID());
        user.setEmail("john@test.com");
        user.setFullName("John Doe");
        user.setPasswordHash("pass123");
        user.setCreatedAt(Instant.now());
        store.put(user.getId(), user);
    }

    @Override
    public Optional<User> findByEmail(String email) {
        return store.values().stream()
                .filter(u -> u.getEmail().equals(email))
                .findFirst();
    }

    @Override
    public Optional<User> findById(UUID id) {
        return Optional.ofNullable(store.get(id));
    }

    @Override
    public User save(User user) {
        if (user.getId() == null) {
            user.setId(UUID.randomUUID());
        }
        store.put(user.getId(), user);
        return user;
    }

    @Override
    public boolean existsByEmail(String email) {
        return store.values().stream()
                .anyMatch(u -> u.getEmail().equals(email));
    }

    @Override
    public void deleteById(UUID id) {
        store.remove(id);
    }
}