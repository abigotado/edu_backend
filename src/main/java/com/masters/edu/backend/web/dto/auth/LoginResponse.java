package com.masters.edu.backend.web.dto.auth;

public record LoginResponse(Long userId, String email, String role, TokenPair tokens) {
}


