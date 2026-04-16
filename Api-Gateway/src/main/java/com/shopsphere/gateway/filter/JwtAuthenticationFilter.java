package com.shopsphere.gateway.filter;

import java.util.List;
import java.util.concurrent.TimeUnit;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.cloud.gateway.filter.GatewayFilterChain;
import org.springframework.cloud.gateway.filter.GlobalFilter;
import org.springframework.core.Ordered;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.http.HttpCookie;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseCookie;
import org.springframework.http.server.reactive.ServerHttpRequest;
import org.springframework.stereotype.Component;
import org.springframework.web.server.ServerWebExchange;

import com.shopsphere.gateway.client.AuthClient;
import com.shopsphere.gateway.utils.JwtUtils;

import io.jsonwebtoken.Claims;
import reactor.core.publisher.Mono;

@Component
public class JwtAuthenticationFilter implements GlobalFilter, Ordered {

	private static final Logger log = LoggerFactory.getLogger(JwtAuthenticationFilter.class);

	private final JwtUtils jwtUtils;
	private final AuthClient authClient;
	private final RedisTemplate<String, String> redisTemplate;

	public JwtAuthenticationFilter(JwtUtils jwtUtils, AuthClient authClient,
			RedisTemplate<String, String> redisTemplate) {
		super();
		this.jwtUtils = jwtUtils;
		this.authClient = authClient;
		this.redisTemplate = redisTemplate;
	}

	@Override
	public Mono<Void> filter(ServerWebExchange exchange, GatewayFilterChain chain) {

		ServerHttpRequest request = exchange.getRequest();
		String path = request.getURI().getPath();

		log.info(" Incoming request: {}", path);

		// Skip public endpoints
		if (isPublicEndpoint(path)) {
			return chain.filter(exchange);
		}

		String accessToken = extractToken(request, "accessToken");

		// CASE 1: No access token → try refresh
		if (accessToken == null) {
			log.warn("No access token → trying refresh");
			return tryRefresh(exchange, chain, request);
		}

		try {
			// Validate token
			Claims claims = jwtUtils.extractAllClaims(accessToken);

			String username = claims.getSubject();
			String role = claims.get("role", String.class);
			String userId = claims.get("userId", String.class);

			String storedToken = redisTemplate.opsForValue().get("access:" + userId);
			
			log.info("Redis Token: {}", storedToken);
			log.info("Request Token: {}", accessToken);
			if (storedToken == null || !storedToken.equals(accessToken)) {
				log.error("Token revoked or invalid for user: {}", userId);
				return unauthorized(exchange);
			}
			log.info("Token valid for user: {}", username);

			ServerHttpRequest mutatedRequest = request.mutate().header("X-User", username).header("X-Role", role)
					.header("X-User-Id", userId).header("X-Internal-Request", "true")
					.header("Authorization", "Bearer " + accessToken).build();

			return chain.filter(exchange.mutate().request(mutatedRequest).build());

		} catch (Exception e) {

			log.warn("Access token expired → refreshing");

			return tryRefresh(exchange, chain, request);
		}
	}

	// ================= REFRESH LOGIC =================

	private Mono<Void> tryRefresh(ServerWebExchange exchange, GatewayFilterChain chain, ServerHttpRequest request) {

		String refreshToken = extractToken(request, "refreshToken");

		
		if (refreshToken == null) {
			log.error("No refresh token → unauthorized");
			return unauthorized(exchange);
		}

		return authClient.refreshToken(refreshToken).flatMap(response -> {

			log.info("Token refreshed successfully");

			String newAccessToken = response.getAccessToken();
			String newRefreshToken = response.getRefreshToken();

//  NOW extract userId from NEW access token
			Claims claims = jwtUtils.extractAllClaims(newAccessToken);

			String username = claims.getSubject();
			String role = claims.get("role", String.class);
			String userId = claims.get("userId", String.class);

//  UPDATE REDIS
			redisTemplate.delete("access:" + userId);
			redisTemplate.opsForValue().set("access:" + userId, newAccessToken, 15, TimeUnit.MINUTES);

//  Set cookies
			exchange.getResponse().addCookie(createCookie("accessToken", newAccessToken, 900));
			exchange.getResponse().addCookie(createCookie("refreshToken", newRefreshToken, 7 * 24 * 60 * 60));

// Forward request
			ServerHttpRequest newRequest = request.mutate().header("X-User", username).header("X-Role", role)
					.header("X-User-Id", userId).header("X-Internal-Request", "true")
					.header("Authorization", "Bearer " + newAccessToken).build();

			return chain.filter(exchange.mutate().request(newRequest).build());
		}).onErrorResume(e -> {
			log.error("Refresh failed: {}", e.getMessage());
			return unauthorized(exchange);
		});
	}
	// ================= HELPERS =================

	private String extractToken(ServerHttpRequest request, String name) {

		List<HttpCookie> cookies = request.getCookies().get(name);

		if (cookies == null || cookies.isEmpty()) {
			return null;
		}

		return cookies.get(0).getValue();
	}

	private boolean isPublicEndpoint(String path) {
		return path.startsWith("/api/auth/login") || path.startsWith("/api/auth/register")
				|| path.startsWith("/api/auth/forgot-password") || path.startsWith("/api/auth/reset-password");
	}

	private ResponseCookie createCookie(String name, String value, long maxAge) {
		return ResponseCookie.from(name, value).httpOnly(true).secure(true).path("/").sameSite("Strict").maxAge(maxAge)
				.build();
	}

	private Mono<Void> unauthorized(ServerWebExchange exchange) {
		exchange.getResponse().setStatusCode(HttpStatus.UNAUTHORIZED);
		return exchange.getResponse().setComplete();
	}

	@Override
	public int getOrder() {
		return -1;
	}
}