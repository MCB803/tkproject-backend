package com.example.tkproject.util;

import io.jsonwebtoken.*;
import io.jsonwebtoken.security.Keys;
import org.springframework.security.core.Authentication;
import org.springframework.stereotype.Component;

import java.security.Key;
import java.util.Date;

@Component
public class JwtTokenProvider {

    // In production, store this secret securely (e.g., in environment variables or a secure vault)
    // For demo purposes, we generate a secret key here.
    private final Key secretKey = Keys.secretKeyFor(SignatureAlgorithm.HS256);

    // Token validity (e.g., 1 hour)
    private final long validityInMilliseconds = 3600000; // 1 hour in milliseconds

    /**
     * Generate a JWT token for the given authenticated user.
     *
     * @param authentication the Authentication object after successful login.
     * @return a signed JWT token.
     */
    public String generateToken(Authentication authentication) {
        String username = authentication.getName();
        Date now = new Date();
        Date expiryDate = new Date(now.getTime() + validityInMilliseconds);

        return Jwts.builder()
                .setSubject(username)
                .setIssuedAt(now)
                .setExpiration(expiryDate)
                // signWith(secretKey) is deprecated in later versions unless you pass the algorithm as well
                .signWith(secretKey, SignatureAlgorithm.HS256)
                .compact();
    }

    /**
     * Validate the given JWT token.
     *
     * @param token the JWT token to validate.
     * @return true if valid; false otherwise.
     */
    public boolean validateToken(String token) {
        try {
            Jwts.parserBuilder()
                    .setSigningKey(secretKey)
                    .build()
                    .parseClaimsJws(token);
            return true;
        } catch (JwtException | IllegalArgumentException e) {
            // Log the exception as needed for debugging
            return false;
        }
    }

    /**
     * Extract the username from the JWT token.
     *
     * @param token the JWT token.
     * @return the username stored in the token.
     */
    public String getUsernameFromJWT(String token) {
        Claims claims = Jwts.parserBuilder()
                .setSigningKey(secretKey)
                .build()
                .parseClaimsJws(token)
                .getBody();
        return claims.getSubject();
    }
}
