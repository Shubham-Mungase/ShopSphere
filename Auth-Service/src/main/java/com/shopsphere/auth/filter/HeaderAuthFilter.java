package com.shopsphere.auth.filter;

import java.io.IOException;
import java.util.List;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Component;
import org.springframework.web.filter.OncePerRequestFilter;

import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;

@Component
public class HeaderAuthFilter extends OncePerRequestFilter {
	private static final Logger log = LoggerFactory.getLogger(HeaderAuthFilter.class);

	@Override
	protected void doFilterInternal(HttpServletRequest request,
	                                HttpServletResponse response,
	                                FilterChain filterChain)
	        throws ServletException, IOException {

	    String user = request.getHeader("X-User");
	    String role = request.getHeader("X-Role");
	    
	    
	    log.info(" Incoming request to Auth Service: {}", request.getRequestURI());

	    if (user != null && role != null) {

	        log.info(" Headers received: user={}, role={}", user, role);

	        var authorities = List.of(
	                new SimpleGrantedAuthority("ROLE_" + role)
	        );

	        var auth = new UsernamePasswordAuthenticationToken(
	                user,
	                null,
	                authorities
	        );

	        SecurityContextHolder.getContext().setAuthentication(auth);

	        log.info(" SecurityContext set for user: {}", user);

	    } else {
	        log.warn(" Missing internal headers. Possible direct access attempt!");
	    }

	    filterChain.doFilter(request, response);
	}
}