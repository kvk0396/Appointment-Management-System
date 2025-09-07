package com.cognizant.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.config.annotation.method.configuration.EnableMethodSecurity;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter; // For filter order

@Configuration
@EnableWebSecurity
@EnableMethodSecurity // Keep this if you use @PreAuthorize annotations
public class SecurityConfig {

    private final HeaderAuthFilter headerAuthFilter;

    public SecurityConfig(HeaderAuthFilter headerAuthFilter) {
        this.headerAuthFilter = headerAuthFilter;
    }

    @Bean
    public SecurityFilterChain securityFilterChain(HttpSecurity http) throws Exception {
        http
            .csrf(csrf -> csrf.disable()) // Correct for stateless REST APIs
            .authorizeHttpRequests(authorize -> authorize
                // Permit login and register endpoints
                .requestMatchers("/api/v1/doctor/login").permitAll()   // <--- THIS IS THE PROBLEM
                .requestMatchers("/api/v1/doctor/register").permitAll() // <--- THIS IS THE PROBLEM
                // All other requests require authentication (will be handled by HeaderAuthFilter)
                .anyRequest().authenticated()
            )
            .sessionManagement(session -> session
                .sessionCreationPolicy(SessionCreationPolicy.STATELESS) // Crucial for stateless APIs
            )
            // Add your custom header authentication filter BEFORE the standard UsernamePasswordAuthenticationFilter
            // This ensures your filter runs early to populate the SecurityContext
            .addFilterBefore(headerAuthFilter, UsernamePasswordAuthenticationFilter.class);

        return http.build();
    }
}