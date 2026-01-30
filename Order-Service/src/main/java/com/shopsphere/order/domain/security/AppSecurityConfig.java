package com.shopsphere.order.domain.security;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.HttpMethod;
import org.springframework.security.config.annotation.method.configuration.EnableMethodSecurity;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;

import com.shopsphere.order.domain.filter.JwtAuthFilter;



@Configuration
@EnableWebSecurity
@EnableMethodSecurity
public class AppSecurityConfig {

    private final JwtAuthFilter jwtAuthFilter;

    public AppSecurityConfig(JwtAuthFilter jwtAuthFilter) {
        this.jwtAuthFilter = jwtAuthFilter;
    }

    @Bean
    SecurityFilterChain filterChain(HttpSecurity http) throws Exception {

    	return http
         .csrf(csrf -> csrf.disable())
         .sessionManagement(session -> session
             .sessionCreationPolicy(SessionCreationPolicy.STATELESS)
         )
         .authorizeHttpRequests(auth -> auth
        	.requestMatchers(HttpMethod.PATCH, "/**").permitAll()
             .requestMatchers("/actuator/**").permitAll()
             .anyRequest().authenticated()
         )
         .addFilterBefore(jwtAuthFilter,
                 UsernamePasswordAuthenticationFilter.class)

            // DISABLE DEFAULT AUTH
            .httpBasic(basic -> basic.disable())
            .formLogin(login -> login.disable())

            .build();
    }
}