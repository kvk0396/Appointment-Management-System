package com.cognizant.security;

import java.util.List;
import java.util.function.Predicate;

import org.springframework.http.server.reactive.ServerHttpRequest;
import org.springframework.stereotype.Component;

@Component
public class RouterValidator {

  
    public static final List<String> openApiEndpoints = List.of(
    		"/api/v1/patient/login",
            "/api/v1/patient/register",
            "/api/v1/doctor/login",
            "/api/v1/doctor/register"
    );

    public Predicate<ServerHttpRequest> isSecured = 
            request -> openApiEndpoints.stream()
                    .noneMatch(uri -> request.getURI().getPath().startsWith(uri));
}