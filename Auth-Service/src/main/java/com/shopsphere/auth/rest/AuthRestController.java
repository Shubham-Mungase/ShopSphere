package com.shopsphere.auth.rest;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.CookieValue;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestHeader;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.shopsphere.auth.dto.ChangePasswordRequest;
import com.shopsphere.auth.dto.ForgotPasswordRequest;
import com.shopsphere.auth.dto.LoginRequest;
import com.shopsphere.auth.dto.LogoutRequest;
import com.shopsphere.auth.dto.RefreshTokenRequest;
import com.shopsphere.auth.dto.RefreshTokenResponse;
import com.shopsphere.auth.dto.RegisterRequest;
import com.shopsphere.auth.dto.ResetPasswordRequest;
import com.shopsphere.auth.dto.TokenResponse;
import com.shopsphere.auth.exception.ApiResponse;
import com.shopsphere.auth.exception.TokenException;
import com.shopsphere.auth.service.CustomerService;
import com.shopsphere.auth.service.RefreshTokenService;
import com.shopsphere.auth.utils.CookieUtil;

import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.validation.Valid;

@RestController
@RequestMapping("/api/auth")
public class AuthRestController {

	private static final Logger log = LoggerFactory.getLogger(AuthRestController.class);

	private final CustomerService customerService;
	private final RefreshTokenService refreshTokenService;
	
	private final RedisTemplate<String, String> redisTemplate;

	
	public AuthRestController(CustomerService customerService, RefreshTokenService refreshTokenService,
			RedisTemplate<String, String> redisTemplate) {
		super();
		this.customerService = customerService;
		this.refreshTokenService = refreshTokenService;
		this.redisTemplate = redisTemplate;
	}
	// ================= REGISTER =================

	

	@PostMapping("/register")
	public ResponseEntity<ApiResponse<Void>> register(@Valid @RequestBody RegisterRequest request) {

		log.info("Register request email={}", request.getEmail());

		customerService.register(request);

		return ResponseEntity.status(HttpStatus.CREATED)
				.body(new ApiResponse<>(true, "User registered successfully", null));
	}

	// ================= LOGIN =================

	@PostMapping("/login")
	public ResponseEntity<ApiResponse<?>> login(@Valid @RequestBody LoginRequest request,
			HttpServletRequest httpRequest, HttpServletResponse response) {

		log.info("Login request email={}", request.getEmail());

		String deviceInfo = httpRequest.getHeader("User-Agent");
		String ipAddress = httpRequest.getRemoteAddr();
		request.setDeviceInfo(deviceInfo);
		request.setIpAddress(ipAddress);
		CookieUtil.clearCookie(response);
		RefreshTokenResponse token = customerService.login(request);
		
		CookieUtil.addAccessTokenCookie(response, token.getAccessToken());
		CookieUtil.addRefreshTokenCookie(response, token.getRefreshToken());

		return ResponseEntity.ok(new ApiResponse<>(true, "Login successful", "token added to cookie"));
	}

	// ================= REFRESH TOKEN =================

	@PostMapping("/refresh")
	public ResponseEntity<TokenResponse> refreshToken(HttpServletResponse response,
			@CookieValue(name = "refreshToken", required = false) String refreshToken) {

		log.info("Refresh token request");
		log.info("Refresh token from cookie: {}", refreshToken);

		if (refreshToken == null) {
			throw new TokenException("Refresh token missing");
		}

		RefreshTokenResponse token = refreshTokenService.refreshToken(new RefreshTokenRequest(refreshToken));

		// Clear old cookies
		CookieUtil.clearCookie(response);

		// Set new cookies
		CookieUtil.addAccessTokenCookie(response, token.getAccessToken());
		CookieUtil.addRefreshTokenCookie(response, token.getRefreshToken());

		// ALSO return tokens in body (IMPORTANT for Gateway)
		TokenResponse responseBody = new TokenResponse(token.getAccessToken(), token.getRefreshToken());

		return ResponseEntity.ok(responseBody);
	}
	// ================= LOGOUT =================

	@PostMapping("/logout")
	public ResponseEntity<ApiResponse<Void>> logout(
			@CookieValue(name = "refreshToken", required = false) String refreshToken, HttpServletResponse response,@RequestHeader(value = "X-User-Id", required = false) String userId
			) {
		redisTemplate.delete("access:" + userId);

		log.info("Logout request");

		if (refreshToken != null) {
			refreshTokenService.logout(new LogoutRequest(refreshToken));
		}

		CookieUtil.clearCookie(response);

		return ResponseEntity.ok(new ApiResponse<>(true, "Logged out successfully", null));
	}
	// ================= LOGOUT ALL =================

	@PostMapping("/logout-all")
	public ResponseEntity<ApiResponse<Void>> logoutAll(HttpServletRequest request, HttpServletResponse response) {

		String userId = request.getHeader("X-User-Id");
		log.info("Logout all request");
		
		redisTemplate.delete("access:"+userId);

		String accessToken = CookieUtil.getCookieValue(request, "accessToken");
		if (accessToken == null) {
			throw new TokenException("Access token missing");
		}

		refreshTokenService.logoutAll(accessToken);

		// CLEAR COOKIES
		CookieUtil.clearCookie(response);

		return ResponseEntity.ok(new ApiResponse<>(true, "Logged out from all devices", null));
	}

	// ================= FORGOT PASSWORD =================

	@PostMapping("/forgot-password")
	public ResponseEntity<ApiResponse<Void>> forgotPassword(@Valid @RequestBody ForgotPasswordRequest request) {

		log.info("Forgot password email={}", request.getEmail());

		customerService.forgotPassword(request);

		return ResponseEntity.ok(new ApiResponse<>(true, "OTP sent to email", null));
	}

	// ================= RESET PASSWORD =================

	@PostMapping("/reset-password")
	public ResponseEntity<ApiResponse<Void>> resetPassword(@Valid @RequestBody ResetPasswordRequest request) {

		log.info("Reset password email={}", request.getEmail());

		customerService.resetPassword(request);

		return ResponseEntity.ok(new ApiResponse<>(true, "Password reset successful", null));
	}

	// ================= CHANGE PASSWORD =================

	@PostMapping("/change-password")
	public ResponseEntity<ApiResponse<Void>> changePassword(@Valid @RequestBody ChangePasswordRequest request) {

		log.info("Change password email={}", request.getEmail());

		customerService.changePassword(request);

		return ResponseEntity.ok(new ApiResponse<>(true, "Password changed successfully", null));
	}

	@GetMapping("/test")
	public ResponseEntity<ApiResponse<Object>> testAuth(
			@RequestHeader(value = "X-User", required = false) String username,
			@RequestHeader(value = "X-Role", required = false) String role,
			@RequestHeader(value = "X-User-Id", required = false) String userId,
			@RequestHeader(value = "X-Internal-Request", required = false) String internalFlag) {

		log.info("Test endpoint hit");

		var response = new java.util.HashMap<String, Object>();
		response.put("username", username);
		response.put("role", role);
		response.put("userId", userId);
		response.put("internalRequest", internalFlag);

		return ResponseEntity.ok(new ApiResponse<>(true, "Auth working", response));
	}

}