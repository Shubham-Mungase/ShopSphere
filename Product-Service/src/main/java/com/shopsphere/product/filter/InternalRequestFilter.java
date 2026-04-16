package com.shopsphere.product.filter;


import java.io.IOException;
import java.util.List;

import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.web.filter.OncePerRequestFilter;

import com.shopsphere.product.dto.UserContext;

import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;

public class InternalRequestFilter extends OncePerRequestFilter {

    @Override
    protected void doFilterInternal(HttpServletRequest request,
                                    HttpServletResponse response,
                                    FilterChain filterChain)
            throws ServletException, IOException {
    	
    	System.out.println("---- HEADERS ----");
    	System.out.println("X-Internal-Request: " + request.getHeader("X-Internal-Request"));
    	System.out.println("X-User: " + request.getHeader("X-User"));
    	System.out.println("X-Role: " + request.getHeader("X-Role"));
    	System.out.println("X-User-Id: " + request.getHeader("X-User-Id"));

        String internal = request.getHeader("X-Internal-Request");

        if (!"true".equalsIgnoreCase(internal)) {
            response.setStatus(HttpServletResponse.SC_FORBIDDEN);
            response.getWriter().write("Forbidden: Direct access not allowed");
            return;
        }

        String username = request.getHeader("X-User");
        String role = request.getHeader("X-Role");
        String userId = request.getHeader("X-User-Id");

        if (username == null || role == null || userId == null) {
            response.setStatus(HttpServletResponse.SC_UNAUTHORIZED);
            response.getWriter().write("Missing user context");
            return;
        }

        List<SimpleGrantedAuthority> authorities =
                List.of(new SimpleGrantedAuthority("ROLE_" + role));

        UsernamePasswordAuthenticationToken auth =
                new UsernamePasswordAuthenticationToken(username, null, authorities);

        auth.setDetails(new UserContext(userId, username, role));

        org.springframework.security.core.context.SecurityContextHolder
                .getContext()
                .setAuthentication(auth);

        filterChain.doFilter(request, response);
    }
}