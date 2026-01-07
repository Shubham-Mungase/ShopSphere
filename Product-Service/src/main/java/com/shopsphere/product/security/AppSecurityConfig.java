package com.shopsphere.product.security;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.HttpMethod;
import org.springframework.security.config.annotation.method.configuration.EnableMethodSecurity;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;

import com.shopsphere.product.filter.JwtAuthFilter;

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

            // JWT = Stateless
            .sessionManagement(sm ->
                sm.sessionCreationPolicy(SessionCreationPolicy.STATELESS))

            .authorizeHttpRequests(auth -> auth

                // ACTUATOR / HEALTH
                .requestMatchers(
                    "/health",
                    "/actuator/health"
                ).permitAll()

                // PUBLIC APIs
                .requestMatchers("/public/**").permitAll()

                // PRODUCT APIs
                .requestMatchers(HttpMethod.GET, "/product/**")
                    .hasAnyRole("USER", "ADMIN")

                    	    .requestMatchers(HttpMethod.POST, "/product/**").hasRole("ADMIN")
                    	    .requestMatchers(HttpMethod.PUT, "/product/**").hasRole("ADMIN")
                    	    .requestMatchers(HttpMethod.DELETE, "/product/**").hasRole("ADMIN")
                    	


                // EVERYTHING ELSE
                .anyRequest().authenticated()
            )

            // JWT FILTER
            .addFilterBefore(
                jwtAuthFilter,
                UsernamePasswordAuthenticationFilter.class
            )

            // DISABLE DEFAULT AUTH
            .httpBasic(basic -> basic.disable())
            .formLogin(login -> login.disable())

            .build();
    }
}
