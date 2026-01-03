package com.shopsphere.auth.security;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.AuthenticationProvider;
import org.springframework.security.authentication.dao.DaoAuthenticationProvider;
import org.springframework.security.config.annotation.authentication.configuration.AuthenticationConfiguration;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;

import com.shopsphere.auth.filter.JwtAuthFilter;

@Configuration
public class AppSecurityConfig {

	private CustomerUserService customerUserService;
	
	private JwtAuthFilter jwtAuthFilter;

	public AppSecurityConfig(CustomerUserService customerUserService, JwtAuthFilter jwtAuthFilter) {
		super();
		this.customerUserService = customerUserService;
		this.jwtAuthFilter = jwtAuthFilter;
	}

	@Bean
	PasswordEncoder passwordEncoder() {
		return new BCryptPasswordEncoder();
	}

	@Bean
	AuthenticationManager authenticationManager(AuthenticationConfiguration config)       	{
		return config.getAuthenticationManager();
	}

	 @Bean
	    SecurityFilterChain filterChain(HttpSecurity http) throws Exception {

	        return http
	            .csrf(csrf -> csrf.disable())
	            .sessionManagement(session ->
	                session.sessionCreationPolicy(SessionCreationPolicy.STATELESS)
	            )
	            .authorizeHttpRequests(auth -> auth
	                .requestMatchers("/auth/register", "/auth/login").permitAll()
	                .requestMatchers("/admin/**").hasRole("ADMIN")
	                .requestMatchers("/manager/**").hasRole("MANAGER")
	                .requestMatchers("/user/**").hasAnyRole("USER","ADMIN","MANAGER")
	                
	            )
	            .authenticationProvider(authenticationProvider())
	            .addFilterBefore(jwtAuthFilter, UsernamePasswordAuthenticationFilter.class)
	            .build();
	    }


	@Bean
	AuthenticationProvider authenticationProvider() {
		DaoAuthenticationProvider provider = new DaoAuthenticationProvider(customerUserService);
		provider.setPasswordEncoder(passwordEncoder());
		return provider;
	}

}
