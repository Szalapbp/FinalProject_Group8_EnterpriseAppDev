package com.bearcat.store_api.service;

import com.bearcat.store_api.dao.UserDao;
import com.bearcat.store_api.entities.User;
import com.bearcat.store_api.service.impl.UserServiceImpl;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.time.Instant;
import java.util.Optional;
import java.util.UUID;

import static org.mockito.Mockito.*;
import static org.junit.jupiter.api.Assertions.*;

@ExtendWith(MockitoExtension.class)
class UserServiceTest {

    @Mock
    private UserDao userDao;

    @InjectMocks
    private UserServiceImpl userService;

    private User testUser;
    private UUID testId;
    private String testEmail;

    @BeforeEach
    void setUp() {
        testId = UUID.randomUUID();
        testEmail = "john@test.com";

        testUser = new User();
        testUser.setId(testId);
        testUser.setEmail(testEmail);
        testUser.setFullName("John Doe");
        testUser.setPasswordHash("pass123");
        testUser.setCreatedAt(Instant.now());
    }

    @Test
    void given_validCredentials_when_authenticateUser_then_returnsTrue() {
        // Given
        String email = "john@test.com";
        String password = "pass123";
        when(userDao.findByEmail(email)).thenReturn(Optional.of(testUser));

        // When
        boolean result = userService.authenticateUser(email, password);

        // Then
        assertTrue(result);
        verify(userDao).findByEmail(email);
    }

    @Test
    void given_invalidPassword_when_authenticateUser_then_returnsFalse() {
        // Given
        String email = "john@test.com";
        String password = "wrongpass";
        when(userDao.findByEmail(email)).thenReturn(Optional.of(testUser));

        // When
        boolean result = userService.authenticateUser(email, password);

        // Then
        assertFalse(result);
        verify(userDao).findByEmail(email);
    }

    @Test
    void given_nonExistingEmail_when_authenticateUser_then_returnsFalse() {
        // Given
        String email = "notfound@test.com";
        String password = "pass123";
        when(userDao.findByEmail(email)).thenReturn(Optional.empty());

        // When
        boolean result = userService.authenticateUser(email, password);

        // Then
        assertFalse(result);
        verify(userDao).findByEmail(email);
    }

    @Test
    void given_validEmail_when_getUserByEmail_then_returnsUser() {
        // Given
        when(userDao.findByEmail(testEmail)).thenReturn(Optional.of(testUser));

        // When
        Optional<User> result = userService.getUserByEmail(testEmail);

        // Then
        assertTrue(result.isPresent());
        assertEquals(testEmail, result.get().getEmail());
        assertEquals("John Doe", result.get().getFullName());
        verify(userDao).findByEmail(testEmail);
    }

    @Test
    void given_invalidEmail_when_getUserByEmail_then_returnsEmpty() {
        // Given
        String email = "notfound@test.com";
        when(userDao.findByEmail(email)).thenReturn(Optional.empty());

        // When
        Optional<User> result = userService.getUserByEmail(email);

        // Then
        assertFalse(result.isPresent());
        verify(userDao).findByEmail(email);
    }

    @Test
    void given_validId_when_getUserById_then_returnsUser() {
        // Given
        when(userDao.findById(testId)).thenReturn(Optional.of(testUser));

        // When
        Optional<User> result = userService.getUserById(testId);

        // Then
        assertTrue(result.isPresent());
        assertEquals(testId, result.get().getId());
        verify(userDao).findById(testId);
    }

    @Test
    void given_invalidId_when_getUserById_then_returnsEmpty() {
        // Given
        UUID invalidId = UUID.randomUUID();
        when(userDao.findById(invalidId)).thenReturn(Optional.empty());

        // When
        Optional<User> result = userService.getUserById(invalidId);

        // Then
        assertFalse(result.isPresent());
        verify(userDao).findById(invalidId);
    }

    @Test
    void given_newUser_when_registerUser_then_returnsCreatedUser() {
        // Given
        String email = "new@test.com";
        String name = "New User";
        String password = "newpass";

        when(userDao.existsByEmail(email)).thenReturn(false);
        when(userDao.save(any(User.class))).thenAnswer(invocation -> {
            User saved = invocation.getArgument(0);
            saved.setId(UUID.randomUUID());
            return saved;
        });

        // When
        User result = userService.registerUser(email, name, password);

        // Then
        assertNotNull(result);
        assertEquals(email, result.getEmail());
        assertEquals(name, result.getFullName());
        assertNotNull(result.getId());
        verify(userDao).existsByEmail(email);
        verify(userDao).save(any(User.class));
    }

    @Test
    void given_existingEmail_when_registerUser_then_throwsException() {
        // Given
        String email = "existing@test.com";
        when(userDao.existsByEmail(email)).thenReturn(true);

        // When/Then
        assertThrows(RuntimeException.class, () ->
                userService.registerUser(email, "Name", "pass")
        );
        verify(userDao).existsByEmail(email);
        verify(userDao, never()).save(any());
    }
}
