package com.shopsphere.payment.filter;

import java.io.IOException;
import java.util.List;

import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.web.filter.OncePerRequestFilter;

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

        String path = request.getRequestURI();


     // 1. BYPASS WEBHOOK (NO AUTH, NO HEADER)
     if (path.startsWith("/api/webhook")) {
         filterChain.doFilter(request, response);
         return;
     }

        //  2. CHECK INTERNAL HEADER
        String internal = request.getHeader("X-Internal-Request");

        if ("true".equals(internal)) {

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

            auth.setDetails(userId);

            org.springframework.security.core.context.SecurityContextHolder
                    .getContext()
                    .setAuthentication(auth);
        }

        //  3. IF NOT INTERNAL → LET JWT HANDLE (DO NOT BLOCK)
        filterChain.doFilter(request, response);
    }
}