package com.masters.edu.backend.security;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

import com.masters.edu.backend.domain.user.User;
import com.masters.edu.backend.domain.user.UserRole;
import com.masters.edu.backend.domain.user.UserStatus;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

class JwtTokenProviderTest {

    private JwtTokenProvider tokenProvider;
    private UserPrincipal principal;

    @BeforeEach
    void setUp() {
        JwtProperties properties = new JwtProperties();
        properties.setSecret("0123456789ABCDEF0123456789ABCDEF0123456789ABCDEF0123456789ABCDEF");
        properties.setExpirationMinutes(5);
        properties.setRefreshExpirationDays(7);
        tokenProvider = new JwtTokenProvider(properties);

        User user = new User();
        user.setId(42L);
        user.setEmail("unit@example.com");
        user.setPasswordHash("hashed");
        user.setFullName("Unit Tester");
        user.setRole(UserRole.STUDENT);
        user.setStatus(UserStatus.ACTIVE);
        principal = UserPrincipal.from(user);
    }

    @Test
    void generateAccessToken_isValidAndContainsUserId() {
        String token = tokenProvider.generateAccessToken(principal);

        assertThat(tokenProvider.validateToken(token, TokenType.ACCESS)).isTrue();
        assertThat(tokenProvider.getUserIdFromToken(token)).isEqualTo(principal.getId());
    }

    @Test
    void differentiateBetweenAccessAndRefreshTokens() {
        String refreshToken = tokenProvider.generateRefreshToken(principal);

        assertThat(tokenProvider.validateToken(refreshToken, TokenType.ACCESS)).isFalse();
        assertThat(tokenProvider.validateToken(refreshToken, TokenType.REFRESH)).isTrue();
    }

    @Test
    void invalidSignatureCausesValidationFailure() {
        String token = tokenProvider.generateAccessToken(principal);
        JwtProperties otherProperties = new JwtProperties();
        otherProperties.setSecret("FEDCBA9876543210FEDCBA9876543210FEDCBA9876543210FEDCBA9876543210");
        otherProperties.setExpirationMinutes(5);
        otherProperties.setRefreshExpirationDays(7);
        JwtTokenProvider otherProvider = new JwtTokenProvider(otherProperties);

        assertThat(otherProvider.validateToken(token, TokenType.ACCESS)).isFalse();
        assertThatThrownBy(() -> otherProvider.getUserIdFromToken(token)).isInstanceOf(RuntimeException.class);
    }
}


