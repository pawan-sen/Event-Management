package com.party.authManagement.service;

import java.time.Duration;
import java.time.LocalDateTime;
import java.util.Map;
import java.util.UUID;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.reactive.function.client.WebClient;
import org.springframework.web.server.ResponseStatusException;

import com.party.authManagement.db.AuthRepo;
import com.party.authManagement.dto.LoginResponse;
import com.party.authManagement.entity.AuthEntity;

import io.jsonwebtoken.JwtException;

@Service
@Transactional
public class AuthService {

    private final AuthRepo authRepository;
    private final JwtService jwtService;
    private final PasswordEncoder passwordEncoder;

    private final WebClient.Builder webClientBuilder;

    public AuthService(AuthRepo authRepository, JwtService jwtService, PasswordEncoder passwordEncoder,
            WebClient.Builder webClientBuilder) {
        this.authRepository = authRepository;
        this.jwtService = jwtService;
        this.passwordEncoder = passwordEncoder;
        this.webClientBuilder = webClientBuilder;
    }

    public LoginResponse authenticateUser(String username, String password) {

        System.out.println("Authenticating user: " + username + " with provided password. " + password);

        String userId = webClientBuilder.build()
                .post()
                .uri("http://usermanagement/user/checkPassword/{username}", username)
                .bodyValue(password)
                .retrieve()
                .bodyToMono(String.class)
                .timeout(Duration.ofSeconds(5))
                .block();

        System.out.println("Received userId from user service: " + userId);

        if (userId == null || userId.isEmpty()
                || userId.equalsIgnoreCase("Incorrect Information")) {
            System.out.println("Authentication failed for user: " + username + ". Invalid credentials provided.");

            return null;
        }

        try {
            System.out.println("Before generating tokens for user: " + username + ". User ID: " + userId);

            String accessToken = jwtService.generateAccessToken(userId.toString(), "USER");
            System.out.println("Generated access token for user: " + username);

            String refreshToken = jwtService.generateRefreshToken(userId.toString());
            System.out.println("Generated refresh token for user: " + username);

            String hash = passwordEncoder.encode(refreshToken);
            AuthEntity entity = new AuthEntity();
            entity.setId(UUID.randomUUID());
            entity.setUserId(UUID.fromString(userId));
            entity.setTokenHash(hash);
            entity.setIssuedAt(LocalDateTime.now());
            entity.setExpiryOn(LocalDateTime.now().plusDays(1));
            entity.setActive(true);
            authRepository.save(entity);

            System.out.println("Authentication successful for user: " + username);
            return new LoginResponse(accessToken, refreshToken, 900, userId, username);
        } catch (Exception e) {
            System.err.println("Error generating tokens: " + e.getMessage());
            e.printStackTrace();
            throw e; // Let controller handle it
        }
    }

    public String refresh(String refreshToken) {

        String userId = jwtService.extractUserId(refreshToken);

        String role = jwtService.extractRole(refreshToken);

        AuthEntity auth = authRepository.findByUserIdAndActiveTrue(UUID.fromString(userId))
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.UNAUTHORIZED, "Session not found"));

        if (!passwordEncoder.matches(refreshToken, auth.getTokenHash()))
            throw new ResponseStatusException(HttpStatus.UNAUTHORIZED, "Invalid refresh token");

        if (auth.getExpiryOn().isBefore(LocalDateTime.now()))
            throw new ResponseStatusException(HttpStatus.UNAUTHORIZED, "Refresh token expired");

        String newToken = jwtService.generateAccessToken(userId, role);

        System.out.println("Generated new access token for user ID: " + userId + " is " + newToken);

        return newToken;
    }

    public void logout(String refreshToken) {
        String hash = passwordEncoder.encode(refreshToken);
        authRepository.deleteByTokenHash(hash);
    }

    public ResponseEntity<?> validate(String token) {

        try {
            jwtService.validateToken(token);
            String userId = jwtService.extractUserId(token);
            String role = jwtService.extractRole(token);

            return ResponseEntity.ok(Map.of("valid", true, "userId", userId, "role", role));
        } catch (JwtException e) {
            return ResponseEntity.internalServerError().body("Error in validating token");
        }
    }
}
