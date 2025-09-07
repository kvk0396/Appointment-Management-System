package com.cognizant.service;

import java.nio.charset.StandardCharsets;
import java.security.Key;
import java.util.Date;
import java.util.List;
 
import javax.crypto.SecretKey;
 
import org.springframework.stereotype.Service;

import com.cognizant.dto.UserDTO;

import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.security.Keys;
 
@Service
public class JwtService {
 
    private final String secretKey = "my-super-secret-key-that-is-at-least-32-chars";
    private final SecretKey key = Keys.hmacShaKeyFor(secretKey.getBytes(StandardCharsets.UTF_8));
 
    public String generateToken(String email, UserDTO user) {
        return Jwts.builder()
            .subject(email)
            .claim("roles", List.of("ROLE_" + user.getRole().name())) // FIXED
            .issuedAt(new Date())
            .expiration(new Date(System.currentTimeMillis() + 1000 * 60 * 60))
            .signWith(key)
            .compact();
    }
}