package com.shopsphere.order.domain.security;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.config.annotation.method.configuration.EnableMethodSecurity;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;

import com.shopsphere.order.domain.filter.InternalRequestFilter;


@Configuration
@EnableMethodSecurity
public class SecurityConfig {

    @Bean
    SecurityFilterChain securityFilterChain(HttpSecurity http) throws Exception {

        return http
                .csrf(csrf -> csrf.disable())

                .authorizeHttpRequests(auth -> auth
                        // USER can create/view own orders
                        .requestMatchers("/api/orders").hasRole("USER")

                        // ADMIN full access
                        .requestMatchers("/api/orders/**").hasRole("ADMIN")

                        .anyRequest().authenticated()
                )

                .addFilterBefore(new InternalRequestFilter(),
                        UsernamePasswordAuthenticationFilter.class)

                .build();
    }
}