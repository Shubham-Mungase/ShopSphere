package com.shopsphere.order.domain.config;

import java.util.UUID;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;

import com.shopsphere.order.domain.filter.UserContext;

import feign.RequestInterceptor;

@Configuration
public class FeignAuthInterceptor {

	@Bean
	public RequestInterceptor internalRequestInterceptor() {
	    return requestTemplate -> {

	        // Always mark internal
	        requestTemplate.header("X-Internal-Request", "true");

	        Authentication auth = SecurityContextHolder.getContext().getAuthentication();

	        if (auth != null && auth.isAuthenticated()) {

	            Object details = auth.getDetails();

	            if (details instanceof UserContext user) {
	                requestTemplate.header("X-User", user.getUsername());
	                requestTemplate.header("X-Role", user.getRole());
	                requestTemplate.header("X-User-Id", user.getUserId().toString());
	            } else if (details instanceof UUID userId) {
	                requestTemplate.header("X-User", auth.getName());
	                requestTemplate.header("X-Role",
	                        auth.getAuthorities().iterator().next().getAuthority().replace("ROLE_", ""));
	                requestTemplate.header("X-User-Id", userId.toString());
	            }
	        }
	};
	}
}