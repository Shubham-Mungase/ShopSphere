package com.shopsphere.cart.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.context.request.RequestContextHolder;
import org.springframework.web.context.request.ServletRequestAttributes;

import feign.RequestInterceptor;
import jakarta.servlet.http.HttpServletRequest;

@Configuration
public class FeignConfig {

	
	@Bean
	public RequestInterceptor internalRequestInterceptor() {
	    return requestTemplate -> {

	        requestTemplate.header("X-Internal-Request", "true");

	        ServletRequestAttributes attrs =
	                (ServletRequestAttributes) RequestContextHolder.getRequestAttributes();

	        if (attrs != null) {
	            HttpServletRequest request = attrs.getRequest();

	            requestTemplate.header("X-User", request.getHeader("X-User"));
	            requestTemplate.header("X-Role", request.getHeader("X-Role"));
	            requestTemplate.header("X-User-Id", request.getHeader("X-User-Id"));
	        }
	    };
	}
}