package com.proj.springsecrest.security;

import com.proj.springsecrest.models.Employee;
import com.proj.springsecrest.models.Employment;
import com.proj.springsecrest.repositories.IEmployeeRepository;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;

import java.util.UUID;

@Service
@RequiredArgsConstructor
public class CustomUserDetailsService implements UserDetailsService {

    private final IEmployeeRepository employeeRepository;

    @Transactional
    public UserDetails loadByUserId(UUID code) {
        Employee user = this.employeeRepository.findByCode(code).orElseThrow(() -> new UsernameNotFoundException("Employee not found with code: "+code));
        return UserPrincipal.create(user);
    }

    @Transactional
    public UserDetails loadUserByUsername(String email) {
        Employee user = employeeRepository.findByEmail(email).orElseThrow(()-> new UsernameNotFoundException("Employee not found with email of "+email));
        return UserPrincipal.create(user);
    }
}
