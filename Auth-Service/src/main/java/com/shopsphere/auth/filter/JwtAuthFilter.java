package com.shopsphere.auth.filter;

import java.io.IOException;

import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.stereotype.Component;
import org.springframework.web.filter.OncePerRequestFilter;

import com.shopsphere.auth.security.CustomerUserService;
import com.shopsphere.auth.utils.JwtUtils;

import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;

@Component
public class JwtAuthFilter extends OncePerRequestFilter{

	private JwtUtils jwtUtils;
	
	private CustomerUserService userService;
	public JwtAuthFilter(JwtUtils jwtUtils, CustomerUserService userService) {
		super();
		this.jwtUtils = jwtUtils;
		this.userService = userService;
	}
	@Override
	protected void doFilterInternal(HttpServletRequest request, HttpServletResponse response, FilterChain filterChain)
			throws ServletException, IOException {
		// get header first
		
		String header = request.getHeader("Authorization");
		
		//get bearer token from header
		if(header!=null && header.startsWith("Bearer "))
		{
			String token = header.substring(7);
			
			String email = jwtUtils.extractUsername(token);
			
			//fetch user and validate token
			if(SecurityContextHolder.getContext().getAuthentication()==null)
			{
				UserDetails userDetails = userService.loadUserByUsername(email);
				
				if (jwtUtils.isTokenValid(token, userDetails)) {
				    UsernamePasswordAuthenticationToken authToken =
				        new UsernamePasswordAuthenticationToken(
				            userDetails,
				            null,
				            userDetails.getAuthorities()
				        );

				    SecurityContextHolder.getContext().setAuthentication(authToken);
				}

			}
			
		}
		filterChain.doFilter(request, response);
		
	}

}
