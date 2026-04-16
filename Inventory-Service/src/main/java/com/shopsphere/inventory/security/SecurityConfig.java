package com.shopsphere.inventory.security;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.config.annotation.method.configuration.EnableMethodSecurity;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;

import com.shopsphere.inventory.filter.InternalRequestFilter;

@Configuration
@EnableMethodSecurity
public class SecurityConfig {

	@Bean
	SecurityFilterChain securityFilterChain(HttpSecurity http) throws Exception {

		return http.csrf(csrf -> csrf.disable())

				.authorizeHttpRequests(auth -> auth
						// External APIs
						.requestMatchers("/api/inventory/**").hasAnyRole("USER", "ADMIN")

						.anyRequest().authenticated())

				// Add filter
				.addFilterBefore(new InternalRequestFilter(), UsernamePasswordAuthenticationFilter.class)

				.build();
	}
}