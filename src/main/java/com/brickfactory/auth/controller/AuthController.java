package com.brickfactory.auth.controller;

import com.brickfactory.auth.dto.JwtAuthenticationResponse;
import com.brickfactory.auth.dto.LoginRequest;
import com.brickfactory.auth.dto.RegisterRequest;
import com.brickfactory.auth.dto.UserProfileDto;
import com.brickfactory.auth.service.AuthService;
import jakarta.validation.Valid;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/auth")
public class AuthController {

    private final AuthService authService;

    public AuthController(AuthService authService) {
        this.authService = authService;
    }

    @PostMapping("/register")
    public ResponseEntity<String> registerUser(@Valid @RequestBody RegisterRequest registerRequest) {
        authService.registerUser(registerRequest);
        return ResponseEntity.ok("User registered successfully!");
    }

    @PostMapping("/login")
    public ResponseEntity<JwtAuthenticationResponse> authenticateUser(@Valid @RequestBody LoginRequest loginRequest) {
        String jwt = authService.loginUser(loginRequest);
        return ResponseEntity.ok(new JwtAuthenticationResponse(jwt));
    }

    @GetMapping("/profile")
    public ResponseEntity<UserProfileDto> getUserProfile(Authentication authentication) {
        // The 'authentication' object is automatically populated by Spring Security
        // after our JWT filter validates the token.
        String email = authentication.getName();
        UserProfileDto userProfile = authService.getUserProfile(email);
        return ResponseEntity.ok(userProfile);
    }

}