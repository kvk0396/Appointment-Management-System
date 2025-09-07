package com.cognizant.config;

//package com.cognizant.security; // Create this package in Doctor-Service

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

 @Override
 protected void doFilterInternal(HttpServletRequest request, HttpServletResponse response, FilterChain filterChain)
	        throws ServletException, IOException {

	    String userEmail = request.getHeader("X-User-Email");
	    String userRoleHeader = request.getHeader("X-User-Role");

	    log.debug("HeaderAuthFilter (Availability-Service): Received X-User-Email: {}, X-User-Role: {}", userEmail, userRoleHeader); // Check incoming headers

	    if (userEmail != null && !userEmail.isEmpty() && userRoleHeader != null && !userRoleHeader.isEmpty()
	        && SecurityContextHolder.getContext().getAuthentication() == null) {

	        try {
	            // ... (your existing logic to find user in repo, if applicable) ...

	            Set<GrantedAuthority> authorities = new HashSet<>();
	            String[] rolesArray = userRoleHeader.split(",");

	            for (String role : rolesArray) {
	                String finalRole = role.trim();
	                if (!finalRole.startsWith("ROLE_")) {
	                    finalRole = "ROLE_" + finalRole;
	                }
	                authorities.add(new SimpleGrantedAuthority(finalRole));
	            }
	            log.debug("HeaderAuthFilter (Availability-Service): Constructed authorities: {}", authorities); // <<< CRITICAL: What roles were constructed?

	            UsernamePasswordAuthenticationToken authentication =
	                    new UsernamePasswordAuthenticationToken(
	                            userEmail, // Principal
	                            null,      // Credentials
	                            authorities
	                    );
	            authentication.setDetails(new WebAuthenticationDetailsSource().buildDetails(request));

	            SecurityContextHolder.getContext().setAuthentication(authentication);
	            log.debug("HeaderAuthFilter (Availability-Service): Spring Security Context populated for user: {} with roles: {}", userEmail, authorities); // <<< CRITICAL: What roles are in the context?

	        } catch (Exception e) {
	            log.error("HeaderAuthFilter (Availability-Service): Error populating security context for email {}: {}", userEmail, e.getMessage(), e);
	        }
	    } else {
	        log.debug("HeaderAuthFilter (Availability-Service): Skipping filter chain for unauthenticated/already authenticated request or missing headers.");
	    }

	    filterChain.doFilter(request, response);
	}
}