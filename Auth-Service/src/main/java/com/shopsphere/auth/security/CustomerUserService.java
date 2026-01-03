package com.shopsphere.auth.security;

import java.util.Optional;

import org.springframework.security.core.userdetails.User;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;

import com.shopsphere.auth.entity.CustomerEntity;
import com.shopsphere.auth.repo.CustomerRepository;

@Service
public class CustomerUserService implements UserDetailsService{

	private CustomerRepository repository;
	
	public CustomerUserService(CustomerRepository repository) {
		super();
		this.repository = repository;
	}

	@Override
	public UserDetails loadUserByUsername(String email) throws UsernameNotFoundException {
		Optional<CustomerEntity> byEmail = repository.findByEmail(email);
		CustomerEntity entity = byEmail.orElseThrow(()-> new UsernameNotFoundException("User is not found with this email :"+email));
		
		return  User.builder().username(entity.getEmail()).password(entity.getPassword()).roles(entity.getRole()).build();
	}

}
