package com.cognizant.security;

import java.util.Date;
import java.util.List; // Import List

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.cloud.gateway.filter.GatewayFilter;
import org.springframework.cloud.gateway.filter.factory.AbstractGatewayFilterFactory;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.server.reactive.ServerHttpRequest;
import org.springframework.stereotype.Component;
import org.springframework.web.server.ServerWebExchange;

import io.jsonwebtoken.Claims;
import reactor.core.publisher.Mono;

@Component
public class AuthenticationFilter extends AbstractGatewayFilterFactory<AuthenticationFilter.Config> {

    @Autowired
    private RouterValidator routeValidator;

    @Autowired
    private JwtService jwtService; // Your JwtService handles token validation and parsing

    public AuthenticationFilter() {
        super(Config.class);
    }

    @Override
    public GatewayFilter apply(Config config) {
        return (exchange, chain) -> {
            ServerHttpRequest request = exchange.getRequest();

            if (routeValidator.isSecured.test(request)) {
                if (!request.getHeaders().containsKey(HttpHeaders.AUTHORIZATION)) {
                    return this.onError(exchange, "Missing Authorization header", HttpStatus.UNAUTHORIZED);
                }

                String authHeader = request.getHeaders().getFirst(HttpHeaders.AUTHORIZATION);
                if (authHeader == null || !authHeader.startsWith("Bearer ")) {
                    return this.onError(exchange, "Invalid Authorization header", HttpStatus.UNAUTHORIZED);
                }

                String token = authHeader.substring(7);

                try {
                    // 1. Validate the token and get claims
                    Claims claims = jwtService.validateToken(token); // This method should return Claims

                    // Check expiration
                    if (claims.getExpiration().before(new Date())) {
                        return this.onError(exchange, "JWT token is expired", HttpStatus.UNAUTHORIZED);
                    }

                    // 2. Extract user details from claims
                    String email = claims.getSubject(); // Assuming email is the subject

                    // --- FIX IS HERE: Retrieve 'roles' as a List<String> ---
                    List<String> rolesList = claims.get("roles", List.class); // Get as List

                    // Convert the list of roles into a single comma-separated string for the header
                    String rolesHeaderValue = (rolesList != null && !rolesList.isEmpty())
                                            ? String.join(",", rolesList)
                                            : ""; // Handle case where roles list might be empty or null

                    // If you have userId in your claims, extract and add it too
                    // For example:
                    // Integer userIdInt = claims.get("userId", Integer.class); // Assuming it's an Integer
                    // String userIdString = (userIdInt != null) ? String.valueOf(userIdInt) : "";


                    // 3. Add user details as custom headers to the request
                    ServerHttpRequest modifiedRequest = request.mutate()
                            .header("X-User-Email", email)
                            .header("X-User-Role", rolesHeaderValue) // Use the joined string for roles
                            // .header("X-User-Id", userIdString) // Add userId if available in claims
                            .build();

                    return chain.filter(exchange.mutate().request(modifiedRequest).build());

                } catch (Exception e) {
                    System.out.println("Invalid access! " + e.getMessage());
                    return this.onError(exchange, "Unauthorized access to the application: " + e.getMessage(), HttpStatus.UNAUTHORIZED);
                }
            }
            // For unsecured routes, just pass through
            return chain.filter(exchange);
        };
    }

    private Mono<Void> onError(ServerWebExchange exchange, String err, HttpStatus httpStatus) {
        exchange.getResponse().setStatusCode(httpStatus);
        return exchange.getResponse().setComplete();
    }

    public static class Config {
        // Can define additional configuration properties here if needed
    }
}