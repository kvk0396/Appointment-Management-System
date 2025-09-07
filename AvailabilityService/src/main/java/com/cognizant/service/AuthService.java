package com.cognizant.service; // Your existing package for AuthService

import com.cognizant.entity.User;
import com.cognizant.enums.Role;
import com.cognizant.repository.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

@Service
public class AuthService {

    private static final Logger log = LoggerFactory.getLogger(AuthService.class);

    @Autowired
    private UserRepository userRepository; // Assumes this is DoctorService's UserRepository

    // You no longer directly inject HttpServletRequest here
    // because HeaderAuthFilter handles populating SecurityContextHolder.

    public User getCurrentAuthenticatedUser() throws Exception {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        if (authentication == null || !authentication.isAuthenticated() || authentication.getPrincipal() == null) {
            log.error("AuthService (Availability-Service): No valid authenticated user found in SecurityContext during getCurrentAuthenticatedUser call.");
            throw new Exception("No authenticated user in context.");
        }

        String authenticatedEmail = authentication.getName(); // Should be the email from HeaderAuthFilter
        log.debug("AuthService (Availability-Service): Authenticated email from SecurityContext: {}", authenticatedEmail);

        // Fetch user from DB (assuming shared DB or local user repo)
        User user = null;
        try {
             user = userRepository.findByEmail(authenticatedEmail)
                    .orElseThrow(() -> {
                        log.error("AuthService (Availability-Service): User with email {} (from SecurityContext) NOT FOUND IN DATABASE. This will cause an error in SpEL.", authenticatedEmail);
                        return new Exception("Authenticated user not found in database."); // Using RuntimeException for direct debugging
                    });
             log.debug("AuthService (Availability-Service): Fetched User from DB: ID={}, Email={}, Role={}", user.getUserId(), user.getEmail(), user.getRole());

             // CRITICAL CHECK: Is userId null?
             if (user.getUserId() == null) {
                 log.error("AuthService (Availability-Service): Fetched User has a NULL userId for email {}. This will cause a NullPointerException in SpEL.", authenticatedEmail);
                 throw new Exception("Authenticated user has a null userId.");
             }

        } catch (Exception e) {
            log.error("AuthService (Availability-Service): Exception during user lookup for email {}: {}", authenticatedEmail, e.getMessage(), e);
            throw e; // Re-throw to see it in logs
        }

        return user;
    }
}