package com.ecommerce.user_service.services;

import com.ecommerce.user_service.entities.UserEntity;
import com.ecommerce.user_service.repositories.UserRepository;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

@Service
public class UserService {
    private final UserRepository userRepository;

    public UserService(UserRepository userRepository) {
        this.userRepository = userRepository;
    }

    public List<UserEntity> allUsers() {
        List<UserEntity> userEntities = new ArrayList<>();

        userRepository.findAll().forEach(userEntities::add);

        return userEntities;
    }

    public Optional<UserEntity> getUserById(Long id) {
        return userRepository.findById(id);
    }
}