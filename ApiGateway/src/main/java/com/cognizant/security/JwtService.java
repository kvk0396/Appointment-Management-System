package com.cognizant.security;

import java.nio.charset.StandardCharsets;
import java.security.Key;

import org.springframework.stereotype.Service;

import io.jsonwebtoken.Claims;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.security.Keys;
@Service
public class JwtService {

    // Your preset secret value (should be loaded from configuration in production)
    private final String SECRET = "my-super-secret-key-that-is-at-least-32-chars";

    

    public Claims validateToken(String token) {
        Key key = Keys.hmacShaKeyFor(SECRET.getBytes(StandardCharsets.UTF_8));
         return Jwts.parserBuilder()
                .setSigningKey(key)
                .build()
                .parseClaimsJws(token)
                .getBody();
    }
}
