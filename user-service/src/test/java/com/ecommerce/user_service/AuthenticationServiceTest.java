package com.ecommerce.user_service;

import com.ecommerce.user_service.dto.LoginUserDto;
import com.ecommerce.user_service.entities.UserEntity;
import com.ecommerce.user_service.exceptions.UserAlreadyExistsException;
import com.ecommerce.user_service.exceptions.UserNotFoundException;
import com.ecommerce.user_service.repositories.UserRepository;
import com.ecommerce.user_service.services.AuthenticationService;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.oauth2.jwt.Jwt;
import org.springframework.security.oauth2.jwt.JwtEncoder;

import java.util.Map;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

class AuthenticationServiceTest {

    @Mock
    private UserRepository userRepository;

    @Mock
    private PasswordEncoder passwordEncoder;

    @Mock
    private AuthenticationManager authenticationManager;

    @Mock
    private JwtEncoder jwtEncoder;

    @Mock
    private Authentication authentication;

    @InjectMocks
    private AuthenticationService authenticationService;

    public AuthenticationServiceTest() {
        MockitoAnnotations.openMocks(this);
    }


    @Test
    void register_UserAlreadyExists() {
        // Mock input
        UserEntity user = new UserEntity("test@example.com", "Test User", "password", "Test Address", "CUSTOMER");

        // Mock repository behavior
        when(userRepository.findByEmail(user.getEmail())).thenReturn(Optional.of(user));

        // Call service and assert exception
        Exception exception = assertThrows(UserAlreadyExistsException.class, () -> authenticationService.register(user));

        assertEquals("User with email test@example.com already exists.", exception.getMessage());
        verify(userRepository).findByEmail(user.getEmail());
        verify(userRepository, never()).save(any(UserEntity.class));
    }

    @Test
    void login_InvalidCredentials() {
        // Mock input
        LoginUserDto loginUser = new LoginUserDto("test@example.com", "password");
        UserEntity userEntity = new UserEntity("test@example.com", "Test User", "password", "Test Address", "CUSTOMER");

        // Mock repository and authentication behavior
        when(userRepository.findByEmail(loginUser.getEmail())).thenReturn(Optional.of(userEntity));
        when(authenticationManager.authenticate(any(UsernamePasswordAuthenticationToken.class)))
                .thenThrow(new BadCredentialsException("Invalid email or password"));

        // Call service and assert exception
        Exception exception = assertThrows(BadCredentialsException.class, () -> authenticationService.login(loginUser));

        assertEquals("Invalid email or password", exception.getMessage());
        verify(userRepository).findByEmail(loginUser.getEmail());
        verify(authenticationManager).authenticate(any(UsernamePasswordAuthenticationToken.class));
    }
}


