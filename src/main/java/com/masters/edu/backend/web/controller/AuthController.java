package com.masters.edu.backend.web.controller;

import com.masters.edu.backend.service.auth.AuthService;
import com.masters.edu.backend.web.dto.auth.LoginRequest;
import com.masters.edu.backend.web.dto.auth.LoginResponse;
import com.masters.edu.backend.web.dto.auth.RefreshTokenRequest;
import com.masters.edu.backend.web.dto.auth.RegisterRequest;
import com.masters.edu.backend.web.dto.auth.TokenPair;

import jakarta.validation.Valid;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/auth")
public class AuthController {

    private final AuthService authService;

    public AuthController(AuthService authService) {
        this.authService = authService;
    }

    @PostMapping("/login")
    public ResponseEntity<LoginResponse> login(@Valid @RequestBody LoginRequest request) {
        return ResponseEntity.ok(authService.login(request));
    }

    @PostMapping("/register")
    public ResponseEntity<LoginResponse> register(@Valid @RequestBody RegisterRequest request) {
        return ResponseEntity.ok(authService.register(request));
    }

    @PostMapping("/refresh")
    public ResponseEntity<TokenPair> refresh(@Valid @RequestBody RefreshTokenRequest request) {
        return ResponseEntity.ok(authService.refresh(request));
    }
}


