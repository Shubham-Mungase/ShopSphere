package com.shopsphere.product.security;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.config.annotation.method.configuration.EnableMethodSecurity;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;

import com.shopsphere.product.filter.InternalRequestFilter;

@Configuration
@EnableMethodSecurity
public class SecurityConfig {

    @Bean
    SecurityFilterChain securityFilterChain(HttpSecurity http) throws Exception {

        return http
                .csrf(csrf -> csrf.disable())

                .authorizeHttpRequests(auth -> auth

                	    // 🔥 ADMIN ONLY (write operations)
                	    .requestMatchers(
                	            "/api/product",             // POST create
                	            "/api/product/*/image",
                	            "/api/product/category"
                	    ).hasRole("ADMIN")

                	    // 🔥 USER + ADMIN (read operations)
                	    .requestMatchers(
                	            "/api/product/**",
                	            "/api/product/category/**"
                	    ).hasAnyRole("USER", "ADMIN")

                	    .anyRequest().authenticated()
                	)

                .addFilterBefore(new InternalRequestFilter(),
                        UsernamePasswordAuthenticationFilter.class)

                .build();
    }
}