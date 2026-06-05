package com.microserv.apigateway.filter;

import java.nio.charset.StandardCharsets;
import java.util.Map;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.cloud.gateway.filter.GlobalFilter;
import org.springframework.core.io.buffer.DataBuffer;
import org.springframework.http.HttpMethod;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.stereotype.Component;
import org.springframework.web.reactive.function.client.WebClient;
import org.springframework.web.server.ServerWebExchange;

import io.jsonwebtoken.Claims;
import io.jsonwebtoken.ExpiredJwtException;
import io.jsonwebtoken.JwtException;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.security.Keys;
import reactor.core.publisher.Mono;

@Component
public class AuthFilter implements GlobalFilter {

    @Value("${jwt.secret}")
    private String secretKey;

    @Autowired
    private WebClient.Builder webClientBuilder;

    @Override
    public Mono<Void> filter(ServerWebExchange exchange,
            org.springframework.cloud.gateway.filter.GatewayFilterChain chain) {
        System.out.println("AuthFilter applied for path: " + exchange.getRequest().getURI().getPath());

        if (isAuthRoute(exchange)) {
            return chain.filter(exchange);
        }

        String authHeader = exchange.getRequest().getHeaders().getFirst("Authorization");
        String refreshTokenHeader = exchange.getRequest().getHeaders().getFirst("X-Refresh-Token");

        if (authHeader == null || !authHeader.startsWith("Bearer ")) {
            return onError(exchange, "Missing Authorization header", HttpStatus.UNAUTHORIZED);
        }

        String accessToken = authHeader.substring(7);

        return validateTokenAndAddHeader(exchange, accessToken, refreshTokenHeader)
                .flatMap(modifiedExchange -> chain.filter(modifiedExchange))
                .onErrorResume(e -> {
                    if (e.getMessage().contains("Admin access required")) {
                        return onError(exchange, "Admin access required", HttpStatus.FORBIDDEN);
                    }
                    System.err.println("Token validation error: " + e.getMessage());
                    e.printStackTrace();
                    return onError(exchange, "Invalid or expired token: " + e.getMessage(), HttpStatus.UNAUTHORIZED);
                });
    }

    private boolean isAdminRoute(String path) {
        return path.contains("/actuator");
    }

    private Mono<ServerWebExchange> validateTokenAndAddHeader(ServerWebExchange exchange, 
            String accessToken, String refreshTokenHeader) {
        try {
            Claims claims = validateToken(accessToken);
            String userId = claims.getSubject();

            // Extract role from claims (adjust based on your JWT structure)
            String role = claims.get("role", String.class); // or claims.get("roles", String.class)
            
            // Check if admin route requires admin role
            if (isAdminRoute(exchange.getRequest().getURI().getPath())) {
                if (!"ADMIN".equalsIgnoreCase(role)) {
                    return Mono.error(new RuntimeException("Admin access required"));
                }
            }

            return Mono.just(addUserIdHeader(exchange, userId));
        } catch (ExpiredJwtException e) {
            System.err.println("Token expired: " + e.getMessage());
            return refreshAndAddNewToken(exchange, refreshTokenHeader);
        }
    }

    private Mono<ServerWebExchange> refreshAndAddNewToken(ServerWebExchange exchange, 
            String refreshTokenHeader) {
        return callAuthServiceToRefresh(refreshTokenHeader)
                .flatMap(newToken -> {
                    newToken = newToken.strip();
                    System.out.println("Received new token from auth service: " + newToken);
                    
                    if (newToken != null && !newToken.isEmpty() && !newToken.equals("null")) {
                        try {
                            exchange.getResponse().getHeaders().add("X-New-Token", newToken);
                            Claims claims = validateToken(newToken);

                            // Extract role from claims (adjust based on your JWT structure)
                            String role = claims.get("role", String.class); // or claims.get("roles", String.class)
                            
                            // Check if admin route requires admin role
                            if (isAdminRoute(exchange.getRequest().getURI().getPath())) {
                                if (!"ADMIN".equalsIgnoreCase(role)) {
                                    return Mono.error(new RuntimeException("Admin access required"));
                                }
                            }

                            return Mono.just(addUserIdHeader(exchange, claims.getSubject()));
                        } catch (Exception e) {
                            return Mono.error(new RuntimeException("Invalid refreshed token"));
                        }
                    } else {
                        return Mono.error(new RuntimeException("Token refresh failed"));
                    }
                })
                .onErrorResume(e -> Mono.error(new RuntimeException("Token expired and refresh failed")));
    }

    private boolean isAuthRoute(ServerWebExchange exchange) {
        String path = exchange.getRequest().getURI().getPath();

        return path.startsWith("/usermanagement/auth") || path.startsWith("/eureka")
                || path.startsWith("/auth")
                || (path.startsWith("/usermanagement/user")
                        && exchange.getRequest().getMethod().equals(HttpMethod.POST));
    }

    private Mono<Void> onError(ServerWebExchange exchange, String err, HttpStatus httpStatus) {
        exchange.getResponse().setStatusCode(httpStatus);
        exchange.getResponse().getHeaders().setContentType(MediaType.APPLICATION_JSON);

        String errorResponse = "{\"error\": \"" + err + "\", \"status\": " + httpStatus.value() + "}";
        DataBuffer dataBuffer = exchange.getResponse().bufferFactory()
                .wrap(errorResponse.getBytes(StandardCharsets.UTF_8));

        return exchange.getResponse().writeWith(Mono.just(dataBuffer));
    }

    private ServerWebExchange addUserIdHeader(ServerWebExchange exchange, String userId) {
        return exchange.mutate().request(
                exchange.getRequest().mutate()
                        .header("X-User-Id", userId)
                        .build())
                .build();
    }

    private Mono<String> callAuthServiceToRefresh(String refreshToken) {
        if (refreshToken == null || refreshToken.isEmpty()) {
            System.err.println("Refresh token is null or empty");
            return Mono.error(new RuntimeException("Refresh token is invalid"));
        }
        
        System.out.println("Attempting to refresh token with auth service...");
        return webClientBuilder.build()
                .post()
                .uri("http://usermanagement/auth/refresh?refreshToken=" + refreshToken)
                .retrieve()
                .bodyToMono(Map.class)
                .doOnNext(response -> System.out.println("Refresh response received: " + response))
                .map(response -> {
                    Object accessTokenObj = response.get("accessToken");
                    if (accessTokenObj == null) {
                        System.err.println("Access token not found in response: " + response);
                        throw new RuntimeException("Access token not in response");
                    }
                    return (String) accessTokenObj;
                })
                .doOnError(error -> System.err.println("Token refresh failed: " + error.getMessage()))
                .timeout(java.time.Duration.ofSeconds(15));
    }

    private Claims validateToken(String token) throws JwtException {
        return Jwts.parserBuilder()
                .setSigningKey(Keys.hmacShaKeyFor(secretKey.getBytes(StandardCharsets.UTF_8)))
                .build()
                .parseClaimsJws(token)
                .getBody();
    }
}
