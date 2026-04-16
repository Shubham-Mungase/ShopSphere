package com.shopsphere.shipping.security;


import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;

import com.shopsphere.shipping.filter.InternalRequestFilter;

@Configuration
public class SecurityConfig {

    @Bean
    SecurityFilterChain securityFilterChain(HttpSecurity http) throws Exception {

        return http
                .csrf(csrf -> csrf.disable())

                .authorizeHttpRequests(auth -> auth
                        // Internal APIs (called via gateway or other services)
                        .requestMatchers("/api/shipments/**").hasAnyRole("USER", "ADMIN")

                        // Allow internal communication
                        .requestMatchers("/api/shipments/internal/**").permitAll()

                        .anyRequest().authenticated()
                )

                // Add internal filter
                .addFilterBefore(new InternalRequestFilter(),
                        UsernamePasswordAuthenticationFilter.class)

                .build();
    }
}