package com.shopsphere.gateway.filter;

import java.nio.charset.StandardCharsets;
import java.security.Key;
import java.time.Instant;
import java.util.List;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.cloud.gateway.filter.GatewayFilterChain;
import org.springframework.cloud.gateway.filter.GlobalFilter;
import org.springframework.core.Ordered;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.stereotype.Component;
import org.springframework.web.server.ServerWebExchange;

import io.jsonwebtoken.Claims;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.security.Keys;
import reactor.core.publisher.Mono;

@Component
public class JwtAuthFilter implements GlobalFilter, Ordered {

    private static final Logger log = LoggerFactory.getLogger(JwtAuthFilter.class);

    @Value("${jwt.secret}")
    private String secret;
    

    private static final List<String> PUBLIC_PATHS = List.of(
            "/api/auth/login",
            "/api/auth/register",
            "/api/auth/refresh"
    );

    @Override
    public Mono<Void> filter(ServerWebExchange exchange, GatewayFilterChain chain) {

        String path = exchange.getRequest().getURI().getPath();
        String method = exchange.getRequest().getMethod().name();

        log.info("➡ Incoming Request: {} {}", method, path);

        // Public APIs
        if (PUBLIC_PATHS.contains(path)) {
            log.info(" Public endpoint accessed: {}", path);
            return chain.filter(exchange);
        }

        String authHeader = exchange.getRequest()
                .getHeaders()
                .getFirst(HttpHeaders.AUTHORIZATION);

        if (authHeader == null || !authHeader.startsWith("Bearer ")) {
            log.warn(" Missing or invalid Authorization header for path: {}", path);
            return onError(exchange, "Missing Authorization Header", HttpStatus.UNAUTHORIZED);
        }

        String token = authHeader.substring(7);

        try {
            Key key = Keys.hmacShaKeyFor(secret.getBytes(StandardCharsets.UTF_8));

            Claims claims = Jwts.parserBuilder()
                    .setSigningKey(key)
                    .build()
                    .parseClaimsJws(token)
                    .getBody();

            String username = claims.getSubject();
            String role = claims.get("role", String.class);
            String userId = claims.get("userId", String.class);

            log.info("Token validated for user: {}, role: {}", username, role);

            if (username == null || role == null || userId == null) {
                log.error(" Missing claims in token");
                return onError(exchange, "Invalid token claims", HttpStatus.UNAUTHORIZED);
            }

            if (claims.getExpiration() != null &&
                    claims.getExpiration().toInstant().isBefore(Instant.now())) {

                log.warn(" Token expired for user: {}", username);
                return onError(exchange, "Token expired", HttpStatus.UNAUTHORIZED);
            }

            //  Role check
            if (path.startsWith("/api/admin") && !"ADMIN".equals(role)) {
                log.warn(" Unauthorized admin access attempt by user: {}", username);
                return onError(exchange, "Forbidden", HttpStatus.FORBIDDEN);
            }

            //  Forward headers
            log.info("Forwarding headers to downstream service: user={}, role={}", username, role);

            ServerWebExchange modifiedExchange = exchange.mutate()
                    .request(r -> r
                            .header("X-User", username)
                            .header("X-Role", role)
                            .header("X-User-Id", userId)
                            .header("X-Internal-Request", "true")
                    )
                    .build();

            return chain.filter(modifiedExchange);

        } catch (Exception e) {
            log.error(" JWT validation failed: {}", e.getMessage());
            return onError(exchange, "Invalid JWT Token", HttpStatus.UNAUTHORIZED);
        }
    }

    private Mono<Void> onError(ServerWebExchange exchange, String message, HttpStatus status) {

        log.error(" Error Response: {} - {}", status, message);

        exchange.getResponse().setStatusCode(status);
        exchange.getResponse().getHeaders().setContentType(MediaType.APPLICATION_JSON);

        String body = String.format(
                "{\"timestamp\":\"%s\",\"status\":%d,\"error\":\"%s\"}",
                Instant.now(),
                status.value(),
                message
        );

        byte[] bytes = body.getBytes(StandardCharsets.UTF_8);

        return exchange.getResponse()
                .writeWith(Mono.just(exchange.getResponse()
                        .bufferFactory()
                        .wrap(bytes)));
    }

    @Override
    public int getOrder() {
        return -1;
    }
}