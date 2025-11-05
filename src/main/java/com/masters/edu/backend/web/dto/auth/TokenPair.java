package com.masters.edu.backend.web.dto.auth;

public record TokenPair(String tokenType, String accessToken, String refreshToken) {
}


