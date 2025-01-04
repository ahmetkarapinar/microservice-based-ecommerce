package com.ecommerce.user_service.services;

import com.ecommerce.user_service.entities.UserEntity;
import com.ecommerce.user_service.repositories.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.User;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;

import java.util.Arrays;
import java.util.stream.Collectors;

@Service
public class CustomUserDetailService implements UserDetailsService {

    @Autowired
    UserRepository userRepository;

    @Override
    public UserDetails loadUserByUsername(String email) throws UsernameNotFoundException {
        UserEntity userEntity = userRepository.findByEmail(email).orElseThrow(() ->
                new UsernameNotFoundException("User not found by given email!"));

        return new User(userEntity.getEmail(), userEntity.getPassword(),
                Arrays.stream(userEntity.getEmail().split("\\|"))
                        .map(SimpleGrantedAuthority::new).collect(Collectors.toList()));
    }
}
