package com.cognizant.config;


import com.cognizant.enums.Role; // Assuming you have this enum
import com.cognizant.entity.User; // Assuming User entity is available
import com.cognizant.repository.UserRepository; // Assuming UserRepository is available
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.web.authentication.WebAuthenticationDetailsSource;
import org.springframework.stereotype.Component;
import org.springframework.web.filter.OncePerRequestFilter;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.IOException;
import java.util.Collection;
import java.util.Collections;
import java.util.HashSet;
import java.util.Optional;
import java.util.Set;

@Component
public class HeaderAuthFilter extends OncePerRequestFilter {

 private static final Logger log = LoggerFactory.getLogger(HeaderAuthFilter.class);

 @Autowired
 private UserRepository userRepository; // To load full user details if needed

 protected void doFilterInternal(HttpServletRequest request, HttpServletResponse response, FilterChain filterChain)
	        throws ServletException, IOException {

	    String userEmail = request.getHeader("X-User-Email");
	    String userRoleHeader = request.getHeader("X-User-Role"); // From Gateway: e.g., "ROLE_PATIENT"

	    log.debug("HeaderAuthFilter: Received X-User-Email: {}, X-User-Role: {}", userEmail, userRoleHeader); // ADD THIS

	    if (userEmail != null && !userEmail.isEmpty() && userRoleHeader != null && !userRoleHeader.isEmpty()
	        && SecurityContextHolder.getContext().getAuthentication() == null) {

	        try {
	            Optional<User> userOptional = userRepository.findByEmail(userEmail);
	            log.debug("HeaderAuthFilter: User found in DB for {}: {}", userEmail, userOptional.isPresent()); // ADD THIS

	            if (userOptional.isPresent()) { // ONLY if user exists in the database
	                User user = userOptional.get();

	                Set<GrantedAuthority> authorities = new HashSet<>();
	                String[] rolesArray = userRoleHeader.split(","); // e.g., rolesArray = ["ROLE_PATIENT"]

	                for (String role : rolesArray) {
	                    // *** THIS IS THE CRITICAL SECTION ***
	                    // Your JwtService already adds "ROLE_" (e.g., "ROLE_PATIENT")
	                    // The Gateway passes this as "ROLE_PATIENT"
	                    // So, 'role.trim()' will be "ROLE_PATIENT"

	                    // If 'role.trim()' is already "ROLE_PATIENT", you should NOT add "ROLE_" again.
	                    // The conditional logic I provided before might be letting an empty string through or double prefixing.
	                    // Let's simplify and make it robust:

	                    String finalRole = role.trim();
	                    if (!finalRole.startsWith("ROLE_")) {
	                        finalRole = "ROLE_" + finalRole; // Add "ROLE_" if it's missing (e.g., if it was just "PATIENT")
	                    }
	                    authorities.add(new SimpleGrantedAuthority(finalRole));
	                }
	                log.debug("HeaderAuthFilter: Constructed authorities: {}", authorities); // <<< THIS IS THE MOST IMPORTANT LOG TO SEE

	                UsernamePasswordAuthenticationToken authentication =
	                        new UsernamePasswordAuthenticationToken(
	                                user.getEmail(), // Principal should be something meaningful, like email
	                                null,            // Credentials (password) are not needed after authentication
	                                authorities      // The roles/authorities
	                        );
	                authentication.setDetails(new WebAuthenticationDetailsSource().buildDetails(request));

	                SecurityContextHolder.getContext().setAuthentication(authentication);
	                log.debug("HeaderAuthFilter: Spring Security Context populated for user: {} with roles: {}", userEmail, authorities); // Change to 'authorities' for exact view

	            } else {
	                log.warn("HeaderAuthFilter: User with email '{}' from Gateway headers not found in Patient Service's database. Denying access.", userEmail);
	            }

	        } catch (Exception e) {
	            log.error("HeaderAuthFilter: Error populating security context from Gateway headers for email {}: {}", userEmail, e.getMessage(), e); // Log full stack trace
	        }
	    } else {
	        log.debug("HeaderAuthFilter: Skipping filter chain for unauthenticated/already authenticated request or missing headers.");
	    }

	    filterChain.doFilter(request, response);
	}
}