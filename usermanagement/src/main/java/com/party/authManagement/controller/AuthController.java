package com.party.authManagement.controller;

import java.util.Map;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import com.party.authManagement.dto.LoginResponse;
import com.party.authManagement.service.AuthService;

@RestController
@RequestMapping("/auth")
public class AuthController {

    private AuthService authService;

    public AuthController(AuthService authService) {
        this.authService = authService;
    }

    // 1, Authenticate username and password and token - login
    @PostMapping("/login/{username}")
    public ResponseEntity<LoginResponse> login(@PathVariable(name = "username") String username,
            @RequestParam String password) {
        try {
            LoginResponse response = authService.authenticateUser(username, password);
            if (response == null) {
                return ResponseEntity.status(HttpStatus.UNAUTHORIZED).build();
            }
            return ResponseEntity.ok(response);
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).build();
        }
    }

    @PostMapping("/refresh")
    public ResponseEntity<?> refresh(@RequestParam String refreshToken) {
        try {
            System.out.println("Received refresh token request");
            if (refreshToken == null || refreshToken.isEmpty()) {
                System.err.println("Refresh token is null or empty");
                return ResponseEntity.status(HttpStatus.BAD_REQUEST)
                        .body(Map.of("error", "Refresh token is required"));
            }
            
            String accessToken = authService.refresh(refreshToken);
            if (accessToken == null || accessToken.isEmpty()) {
                System.err.println("Failed to generate new access token");
                return ResponseEntity.status(HttpStatus.UNAUTHORIZED)
                        .body(Map.of("error", "Failed to refresh token"));
            }
            
            System.out.println("Successfully refreshed token");
            return ResponseEntity.ok().body(
                    Map.of("accessToken", accessToken, "expiresIn", 900));
        } catch (Exception e) {
            System.err.println("Error during token refresh: " + e.getMessage());
            e.printStackTrace();
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(Map.of("error", "Token refresh failed: " + e.getMessage()));
        }
    }

    @PostMapping("/logout")
    public ResponseEntity<String> logout(@RequestParam String refreshToken) {
        authService.logout(refreshToken);
        return ResponseEntity.ok("Logged out successfully");
    }

    @GetMapping("/validate")
    public ResponseEntity<?> validate(@RequestParam String token) {
        return authService.validate(token);
    }
}
