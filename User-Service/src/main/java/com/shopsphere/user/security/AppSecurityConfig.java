package com.shopsphere.user.security;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;

import com.shopsphere.user.filter.JwtAuthFilter;

@Configuration
	@EnableWebSecurity
	public class AppSecurityConfig {

	    private final JwtAuthFilter jwtAuthFilter;

	    public AppSecurityConfig(JwtAuthFilter jwtAuthFilter) {
	        this.jwtAuthFilter = jwtAuthFilter;
	    }

	    @Bean
	    SecurityFilterChain filterChain(HttpSecurity http) throws Exception {

	        return http
	            .csrf(csrf -> csrf.disable())
	            .sessionManagement(sm ->
	                sm.sessionCreationPolicy(SessionCreationPolicy.STATELESS))
	            .authorizeHttpRequests(auth -> auth
	                .requestMatchers("/health").permitAll()
	                .anyRequest().authenticated()
	            )
	            .addFilterBefore(jwtAuthFilter,
	                UsernamePasswordAuthenticationFilter.class)
	            .httpBasic(basic->basic.disable())
	            .formLogin(login->login.disable())
	            .build();
	    }
	
}
