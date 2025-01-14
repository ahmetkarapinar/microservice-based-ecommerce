package com.ecommerce.user_service;

import com.ecommerce.user_service.entities.UserEntity;
import com.ecommerce.user_service.exceptions.UserNotFoundException;
import com.ecommerce.user_service.repositories.UserRepository;
import com.ecommerce.user_service.services.UserService;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;

import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

class UserServiceTest {

    @Mock
    private UserRepository userRepository;

    @InjectMocks
    private UserService userService;

    public UserServiceTest() {
        MockitoAnnotations.openMocks(this);
    }

    @Test
    void updateUser_Success() {
        // Mock input
        Long userId = 1L;
        UserEntity existingUser = new UserEntity("oldemail@example.com", "Old Name", "password", "Old Address", "CUSTOMER");
        existingUser.setId(userId);
        UserEntity updatedUser = new UserEntity("newemail@example.com", "New Name", "newpassword", "New Address", "ADMIN");

        // Mock repository behavior
        when(userRepository.findById(userId)).thenReturn(Optional.of(existingUser));
        when(userRepository.save(any(UserEntity.class))).thenReturn(existingUser);

        // Call service
        UserEntity result = userService.updateUser(userId, updatedUser);

        // Assertions
        assertEquals("newemail@example.com", result.getEmail());
        assertEquals("New Name", result.getFullName());
        assertEquals("New Address", result.getAddress());
        assertEquals("ADMIN", result.getRole());

        verify(userRepository).findById(userId);
        verify(userRepository).save(existingUser);
    }

    @Test
    void updateUser_UserNotFound() {
        // Mock input
        Long userId = 1L;
        UserEntity updatedUser = new UserEntity("newemail@example.com", "New Name", "newpassword", "New Address", "ADMIN");

        // Mock repository behavior
        when(userRepository.findById(userId)).thenReturn(Optional.empty());

        // Call service and assert exception
        Exception exception = assertThrows(UserNotFoundException.class, () -> userService.updateUser(userId, updatedUser));

        assertEquals("User with ID 1 not found", exception.getMessage());
        verify(userRepository).findById(userId);
        verify(userRepository, never()).save(any(UserEntity.class));
    }
}

