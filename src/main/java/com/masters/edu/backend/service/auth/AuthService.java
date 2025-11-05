package com.masters.edu.backend.service.auth;

import com.masters.edu.backend.domain.user.User;
import com.masters.edu.backend.domain.user.UserRole;
import com.masters.edu.backend.domain.user.UserStatus;
import com.masters.edu.backend.repository.user.UserRepository;
import com.masters.edu.backend.security.JwtTokenProvider;
import com.masters.edu.backend.security.TokenType;
import com.masters.edu.backend.security.UserDetailsServiceImpl;
import com.masters.edu.backend.security.UserPrincipal;
import com.masters.edu.backend.web.dto.auth.LoginRequest;
import com.masters.edu.backend.web.dto.auth.LoginResponse;
import com.masters.edu.backend.web.dto.auth.RefreshTokenRequest;
import com.masters.edu.backend.web.dto.auth.RegisterRequest;
import com.masters.edu.backend.web.dto.auth.TokenPair;

import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import jakarta.validation.ValidationException;

@Service
@Transactional
public class AuthService {

    private final AuthenticationManager authenticationManager;
    private final JwtTokenProvider tokenProvider;
    private final PasswordEncoder passwordEncoder;
    private final UserRepository userRepository;
    private final UserDetailsServiceImpl userDetailsService;

    public AuthService(AuthenticationManager authenticationManager,
            JwtTokenProvider tokenProvider,
            PasswordEncoder passwordEncoder,
            UserRepository userRepository,
            UserDetailsServiceImpl userDetailsService) {
        this.authenticationManager = authenticationManager;
        this.tokenProvider = tokenProvider;
        this.passwordEncoder = passwordEncoder;
        this.userRepository = userRepository;
        this.userDetailsService = userDetailsService;
    }

    public LoginResponse login(LoginRequest request) {
        Authentication authentication = authenticationManager.authenticate(
                new UsernamePasswordAuthenticationToken(request.email(), request.password()));
        UserPrincipal principal = (UserPrincipal) authentication.getPrincipal();
        TokenPair tokens = issueTokens(principal);
        return new LoginResponse(principal.getId(), principal.getEmail(), principal.getRole(), tokens);
    }

    public LoginResponse register(RegisterRequest request) {
        if (userRepository.existsByEmail(request.email())) {
            throw new ValidationException("Email already registered");
        }

        User user = new User();
        user.setEmail(request.email().toLowerCase());
        user.setPasswordHash(passwordEncoder.encode(request.password()));
        user.setFullName(request.fullName());
        user.setRole(UserRole.STUDENT);
        user.setStatus(UserStatus.ACTIVE);
        User saved = userRepository.save(user);

        UserPrincipal principal = UserPrincipal.from(saved);
        TokenPair tokens = issueTokens(principal);
        return new LoginResponse(principal.getId(), principal.getEmail(), principal.getRole(), tokens);
    }

    public TokenPair refresh(RefreshTokenRequest request) {
        if (!tokenProvider.validateToken(request.refreshToken(), TokenType.REFRESH)) {
            throw new ValidationException("Invalid refresh token");
        }
        Long userId = tokenProvider.getUserIdFromToken(request.refreshToken());
        UserDetails userDetails = userDetailsService.loadUserById(userId);
        return issueTokens((UserPrincipal) userDetails);
    }

    private TokenPair issueTokens(UserPrincipal principal) {
        String accessToken = tokenProvider.generateAccessToken(principal);
        String refreshToken = tokenProvider.generateRefreshToken(principal);
        return new TokenPair("Bearer", accessToken, refreshToken);
    }
}


