package com.ecommerce.user_service.controllers;

import com.ecommerce.user_service.entities.UserEntity;
import com.ecommerce.user_service.repositories.UserRepository;
import com.ecommerce.user_service.services.UserService;
import jakarta.websocket.server.PathParam;
import org.springframework.http.ResponseEntity;
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
    public ResponseEntity<List<UserEntity>> allUsers() {
        List <UserEntity> userEntities = userService.allUsers();

        return ResponseEntity.ok(userEntities);
    }
}
