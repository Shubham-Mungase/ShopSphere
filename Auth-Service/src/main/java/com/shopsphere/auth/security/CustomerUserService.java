package com.shopsphere.auth.security;

import org.springframework.security.core.userdetails.User;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;

import com.shopsphere.auth.entity.CustomerEntity;
import com.shopsphere.auth.repo.CustomerRepository;

@Service
public class CustomerUserService implements UserDetailsService {

    private final CustomerRepository repository;

    public CustomerUserService(CustomerRepository repository) {
        this.repository = repository;
    }

    @Override
    public UserDetails loadUserByUsername(String email) throws UsernameNotFoundException {

        CustomerEntity entity = repository.findByEmail(email)
                .orElseThrow(() -> new UsernameNotFoundException("User not found: " + email));

        return User.builder()
                .username(entity.getEmail())
                .password(entity.getPassword())
                .roles(entity.getRole()) // ROLE_USER / ROLE_ADMIN
                .build();
    }
}