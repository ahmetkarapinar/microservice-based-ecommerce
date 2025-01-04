package com.ecommerce.user_service.controllers;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
public class AdminController {

    @GetMapping("/admin/dashboard")
    public ResponseEntity<String> adminDashboard() {
        return ResponseEntity.ok("Welcome to Admin Dashboard!");
    }

    @GetMapping("/customer/profile")
    public ResponseEntity<String> customerProfile() {
        return ResponseEntity.ok("Welcome to Customer Profile!");
    }
}

