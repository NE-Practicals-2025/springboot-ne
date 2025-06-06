package com.proj.springsecrest.configs;

import com.proj.springsecrest.payload.response.ApiResponse;
import com.proj.springsecrest.security.CustomUserDetailsService;
import com.proj.springsecrest.security.JwtAuthenticationEntryPoint;
import com.proj.springsecrest.security.JwtAuthenticationFilter;
import com.fasterxml.jackson.databind.ObjectMapper;
import jakarta.servlet.ServletOutputStream;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.MediaType;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.AuthenticationProvider;
import org.springframework.security.authentication.dao.DaoAuthenticationProvider;
import org.springframework.security.config.annotation.authentication.configuration.AuthenticationConfiguration;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.annotation.web.configurers.AbstractHttpConfigurer;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.web.AuthenticationEntryPoint;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.access.AccessDeniedHandler;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;

@Configuration
@EnableWebSecurity
@RequiredArgsConstructor
public class SecurityConfig {

    private final JwtAuthenticationEntryPoint authenticationEntryPoint;
    private final CustomUserDetailsService userDetailsService;
    private final JwtAuthenticationFilter jwtAuthenticationFilter; // ✅ Inject the filter properly

    @Bean
    public SecurityFilterChain securityFilterChain(HttpSecurity http) throws Exception {
        http.csrf(AbstractHttpConfigurer::disable)
                .authorizeHttpRequests(request -> request
                        // Public endpoints
                        .requestMatchers(
                                "/api/v1/auth/admin/register",
                                "/api/v1/auth/login",
                                "/api/v1/auth/forgot-password",
                                "/api/v1/auth/verify-account/",
                                "/api/v1/auth/reset-password",
                                "/api/v1/auth/request-verification",
                                "/api/v1/auth/verify-account/**",
                                "/api/v1/files/load-file/**",
                                "/swagger-ui/**",
                                "/v3/api-docs/**",
                                "/actuator/**"
                        ).permitAll()

                        // Manager-only endpoints
                        .requestMatchers("/api/v1/employees/**").hasRole("MANAGER")
                        .requestMatchers("/api/v1/employment/**").hasRole("MANAGER")
                        .requestMatchers("/api/v1/deductions/**").hasRole("MANAGER")
                        .requestMatchers("/api/v1/payroll/**").hasRole("MANAGER")
                        .requestMatchers("/api/v1/auth/manager/register").hasRole("ADMIN")

                        // Admin-only endpoints
                        .requestMatchers("/api/v1/payslips/approve/**").hasRole("ADMIN")

                        // Payslip viewing (manager or admin)
                        .requestMatchers("/api/v1/payslips/view/**").hasAnyRole("MANAGER", "ADMIN")

                        // Employee-only endpoints
                        .requestMatchers("/api/v1/payslips/mine/**").hasRole("EMPLOYEE")

                        // Any other request requires authentication
                        .anyRequest().authenticated()
                )
                .sessionManagement(manager -> manager.sessionCreationPolicy(SessionCreationPolicy.STATELESS))
                .authenticationProvider(authenticationProvider())
                .addFilterBefore(jwtAuthenticationFilter, UsernamePasswordAuthenticationFilter.class)
                .exceptionHandling(exceptions -> {
                    exceptions.authenticationEntryPoint(authenticationEntryPoint);
                    exceptions.accessDeniedHandler(accessDeniedHandler());
                });

        return http.build();
    }

    @Bean
    public AuthenticationProvider authenticationProvider() {
        DaoAuthenticationProvider authenticationProvider = new DaoAuthenticationProvider();
        authenticationProvider.setUserDetailsService(userDetailsService);
        authenticationProvider.setPasswordEncoder(passwordEncoder());
        return authenticationProvider;
    }

    @Bean
    public PasswordEncoder passwordEncoder() {
        return new BCryptPasswordEncoder();
    }

    @Bean
    public AuthenticationManager authenticationManager(AuthenticationConfiguration config) throws Exception {
        return config.getAuthenticationManager();
    }

    @Bean
    public AuthenticationEntryPoint authenticationErrorHandler() {
        return (request, response, ex) -> {
            response.setStatus(HttpServletResponse.SC_UNAUTHORIZED);
            response.setContentType(MediaType.APPLICATION_JSON_VALUE);
            ServletOutputStream out = response.getOutputStream();
            new ObjectMapper().writeValue(out, ApiResponse.error("Invalid or missing auth token."));
            out.flush();
        };
    }

    @Bean
    public AccessDeniedHandler accessDeniedHandler() {
        return (request, response, ex) -> {
            response.setStatus(HttpServletResponse.SC_FORBIDDEN);
            response.setContentType(MediaType.APPLICATION_JSON_VALUE);
            ServletOutputStream out = response.getOutputStream();
            new ObjectMapper().writeValue(out, ApiResponse.error("You are not allowed to access this resource."));
            out.flush();
        };
    }
}
