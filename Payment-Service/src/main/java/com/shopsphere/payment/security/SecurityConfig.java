package com.shopsphere.payment.security;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;

import com.shopsphere.payment.filter.InternalRequestFilter;

@Configuration
public class SecurityConfig {

    @Bean
    SecurityFilterChain securityFilterChain(HttpSecurity http) throws Exception {

        return http
                .csrf(csrf -> csrf.disable())

                .authorizeHttpRequests(auth -> auth

                	    //  ALLOW RAZORPAY WEBHOOK (PUBLIC)
                	    .requestMatchers("/api/webhook/**").permitAll()
                	    .requestMatchers("/api/payments/index").permitAll()

                	    //  INTERNAL APIs
                	    .requestMatchers("/api/payments/internal/**").permitAll()

                	    //  USER APIs
                	    .requestMatchers("/api/payments/**").hasAnyRole("USER", "ADMIN")

                	    .anyRequest().authenticated()
                	)
                .addFilterBefore(new InternalRequestFilter(),
                        UsernamePasswordAuthenticationFilter.class)

                .build();
    }
}