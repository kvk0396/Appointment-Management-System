package com.cognizant.service; // Your existing package for AuthService

import com.cognizant.entity.User;
import com.cognizant.enums.Role;
import com.cognizant.exceptions.UserNotFoundException;
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
    private UserRepository userRepo; // Assumes this is DoctorService's UserRepository

    // You no longer directly inject HttpServletRequest here
    // because HeaderAuthFilter handles populating SecurityContextHolder.

    public User getCurrentAuthenticatedUser() {
        // Get the Authentication object from the SecurityContextHolder
        // This will now be populated by your HeaderAuthFilter
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();

        if (authentication == null || !authentication.isAuthenticated() || "anonymousUser".equals(authentication.getPrincipal())) {
            log.warn("Attempt to get authenticated user failed: No authentication found in SecurityContext.");
            throw new UserNotFoundException("User not authenticated.");
        }

        // The principal is typically the username (email in this case)
        String authenticatedEmail = authentication.getName(); // Get the principal's name (email)

        log.debug("Authenticated user email from SecurityContext: {}", authenticatedEmail);

        // Load the full User entity from your repository based on the email.
        // This ensures you get the userId and the actual Role enum from your database,
        // and that the user still exists in this service's domain.
        return userRepo.findByEmail(authenticatedEmail)
                .orElseThrow(() -> {
                    log.error("Authenticated user with email {} (from SecurityContext) not found in Doctor Service's database. Data inconsistency!", authenticatedEmail);
                    return new UserNotFoundException("Authenticated user not found in database.");
                });
    }
}