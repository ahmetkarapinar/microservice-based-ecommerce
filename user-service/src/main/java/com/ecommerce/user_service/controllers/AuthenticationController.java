package com.ecommerce.user_service.controllers;


import com.ecommerce.user_service.dto.LoginUserDto;
import com.ecommerce.user_service.entities.UserEntity;
import com.ecommerce.user_service.services.AuthenticationService;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;

import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RequestMapping("/auth")
@RestController
public class AuthenticationController {
    @Autowired
    private AuthenticationService service;

    @PostMapping("/register")
    public ResponseEntity<?> registerUser(
            @Valid @RequestBody UserEntity user
    ){
        return ResponseEntity.ok(service.register(user));
    }
    @PostMapping("/login")
    public ResponseEntity<?> loginUser(@RequestBody LoginUserDto loginUserDto){
        return ResponseEntity.ok(service.login(loginUserDto));
    }
}

