package com.ecommerce.user_service.services;

import com.ecommerce.user_service.dto.LoginUserDto;
import com.ecommerce.user_service.entities.UserEntity;
import com.ecommerce.user_service.repositories.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.oauth2.jwt.JwtClaimsSet;
import org.springframework.security.oauth2.jwt.JwtEncoder;
import org.springframework.security.oauth2.jwt.JwtEncoderParameters;
import org.springframework.stereotype.Service;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;

import java.time.Instant;
import java.util.HashMap;
import java.util.Map;
import java.util.Optional;
import java.util.stream.Collectors;

@Service
public class AuthenticationService {
    @Autowired
    private UserRepository userRepository;

    @Autowired
    AuthenticationManager authenticationManager;

    @Autowired
    JwtEncoder jwtEncoder;

    @Autowired
    private PasswordEncoder passwordEncoder;

    public String register(UserEntity user){
        Optional<UserEntity> userEntity = userRepository.findByEmail(user.getEmail());
        if (userEntity.isPresent()){
            return "Username already taken";
        }
        user.setPassword(passwordEncoder.encode(user.getPassword()));
        userRepository.save(user);

        return "User Registered successfully";
    }

    public Map<String, Object> login(LoginUserDto user) {
        Optional<UserEntity> userEntity = userRepository.findByEmail(user.getEmail());
        Map<String, Object> response = new HashMap<>();

        Authentication authentication = authenticationManager.authenticate(new UsernamePasswordAuthenticationToken(user.getEmail(), user.getPassword()));
        if (!userEntity.isPresent()){
            response.put("status", "User Not Found");
            return response;
        }
        String accessToken = generateToken(userEntity.get(), authentication, 3600);
        response.put("access_token", accessToken);
        response.put("expires_in", 3600);
        return response;
    }

    private String generateToken(UserEntity userEntity, Authentication authentication, long expiryDuration){
        Instant now = Instant.now();
        JwtClaimsSet claimsSet = JwtClaimsSet.builder()
                .issuer("Karapinar")
                .issuedAt(now)
                .expiresAt(now.plusSeconds(expiryDuration))
                .subject(authentication.getName())
                .claim("role", authentication.getAuthorities().stream()
                        .map(GrantedAuthority::getAuthority)
                        .collect(Collectors.toSet()))
                .claim("email", userEntity.getEmail())
                .claim("userId", userEntity.getId())
                .build();

        return jwtEncoder.encode(JwtEncoderParameters.from(claimsSet)).getTokenValue();

    }
}
