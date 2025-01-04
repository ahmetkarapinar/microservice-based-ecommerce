package com.ecommerce.user_service.controllers;

import com.ecommerce.user_service.entities.UserEntity;
import com.ecommerce.user_service.services.UserService;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RequestMapping("/users")
@RestController
public class UserController {
    private final UserService userService;

    public UserController(UserService userService) {
        this.userService = userService;
    }

    @GetMapping("/me")
    public ResponseEntity<UserEntity> authenticatedUser() {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();

            UserEntity currentUserEntity = (UserEntity) authentication.getPrincipal();
            if (currentUserEntity == null) {
                throw new UsernameNotFoundException("Not authenticated");
            }
            return ResponseEntity.ok(currentUserEntity);
    }

    @GetMapping("/")
    public ResponseEntity<List<UserEntity>> allUsers() {
        List <UserEntity> userEntities = userService.allUsers();

        return ResponseEntity.ok(userEntities);
    }
}
