package com.brickfactory.auth.service;

import com.brickfactory.auth.dto.LoginRequest;
import com.brickfactory.auth.dto.RegisterRequest;
import com.brickfactory.auth.dto.UserProfileDto;
import com.brickfactory.auth.entity.Role;
import com.brickfactory.auth.entity.User;
import com.brickfactory.auth.repository.RoleRepository;
import com.brickfactory.auth.repository.UserRepository;
import com.brickfactory.auth.security.JwtTokenProvider;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import java.util.Collections;

@Service
public class AuthService {

    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;
    private final AuthenticationManager authenticationManager;
    private final JwtTokenProvider tokenProvider;
    private final RoleRepository roleRepository; // Add this



    // Constructor-based dependency injection
    public AuthService(UserRepository userRepository, PasswordEncoder passwordEncoder, AuthenticationManager authenticationManager, JwtTokenProvider tokenProvider, RoleRepository roleRepository) {
        this.userRepository = userRepository;
        this.passwordEncoder = passwordEncoder;
        this.authenticationManager = authenticationManager;
        this.tokenProvider = tokenProvider;
        this.roleRepository = roleRepository;

    }

    public User registerUser(RegisterRequest request) {
        // 1. Check if user already exists
        if (userRepository.findByEmail(request.getEmail()).isPresent()) {
            throw new IllegalStateException("Email already in use");
        }

        // 2. Create a new user object
        User user = new User();
        user.setFullName(request.getFullName());
        user.setEmail(request.getEmail());

        Role userRole = roleRepository.findByName("ROLE_USER").orElseThrow(()-> new RuntimeException("Role not found"));

        user.setRoles(Collections.singleton(userRole));

        // 3. Encode the password before saving
        user.setPasswordHash(passwordEncoder.encode(request.getPassword()));

        // 4. Save the user to the database
        return userRepository.save(user);
    }
    // Add this new method
    public String loginUser(LoginRequest loginRequest) {
        Authentication authentication = authenticationManager.authenticate(
                new UsernamePasswordAuthenticationToken(
                        loginRequest.getEmail(),
                        loginRequest.getPassword()
                )
        );

        SecurityContextHolder.getContext().setAuthentication(authentication);
        return tokenProvider.generateToken(authentication);
    }

    public UserProfileDto getUserProfile(String email) {
        User user = userRepository.findByEmail(email)
                .orElseThrow(() -> new UsernameNotFoundException("User not found"));

        UserProfileDto userProfile = new UserProfileDto();
        userProfile.setId(user.getId());
        userProfile.setEmail(user.getEmail());
        userProfile.setFullName(user.getFullName());
        userProfile.setCreatedAt(user.getCreatedAt());

        return userProfile;
    }

}