package com.shopsphere.user.security;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;

import com.shopsphere.user.filter.InternalRequestFilter;

@Configuration
public class SecurityConfig {

    @Bean
    SecurityFilterChain securityFilterChain(HttpSecurity http) throws Exception {

        return http
                .csrf(csrf -> csrf.disable())

                .authorizeHttpRequests(auth -> auth
                        .requestMatchers("/api/admin/**").hasRole("ADMIN")
                        .requestMatchers("/api/users/**").hasAnyRole("USER", "ADMIN")
                        .requestMatchers("/api/users/internal/**").permitAll()
                        //.requestMatchers("/")
                        .anyRequest().authenticated()
                )

                //  Add our custom filter BEFORE Spring Security
                .addFilterBefore(new InternalRequestFilter(),
                        UsernamePasswordAuthenticationFilter.class)

                .build();
    }

    
}