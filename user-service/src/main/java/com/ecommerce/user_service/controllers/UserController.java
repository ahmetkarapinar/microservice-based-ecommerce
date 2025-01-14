package com.ecommerce.user_service.controllers;

import com.ecommerce.user_service.entities.UserEntity;
import com.ecommerce.user_service.repositories.UserRepository;
import com.ecommerce.user_service.services.UserService;
import jakarta.validation.Valid;
import jakarta.websocket.server.PathParam;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RequestMapping("/users")
@RestController
public class UserController {

    private final UserService userService;

    public UserController(UserService userService) {
        this.userService = userService;
    }

//    @GetMapping("/{id}")
//    public ResponseEntity<UserEntity> getUser(@PathVariable Long id) {
//        return userService
//    }

    @GetMapping("/")
    @PreAuthorize("hasRole('ADMIN')") // Restrict this endpoint to admins
    public ResponseEntity<List<UserEntity>> allUsers() {
        List <UserEntity> userEntities = userService.allUsers();

        return ResponseEntity.ok(userEntities);
    }

    @PutMapping("/{userId}")
    public ResponseEntity<UserEntity> updateUser(
            @PathVariable Long userId,
            @Valid @RequestBody UserEntity updatedUser) {
        UserEntity user = userService.updateUser(userId, updatedUser);
        return ResponseEntity.ok(user);
    }
}
