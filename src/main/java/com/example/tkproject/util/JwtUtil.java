package com.example.tkproject.util;

import io.jsonwebtoken.*;
import io.jsonwebtoken.security.Keys;
import org.springframework.security.core.Authentication;
import org.springframework.stereotype.Component;

import java.security.Key;
import java.util.Date;
import java.util.List;
import java.util.stream.Collectors;

@Component
public class JwtUtil {

    // In production, load the secret key from a secure location (e.g. application.properties or environment variable)
    private final Key secretKey = Keys.secretKeyFor(SignatureAlgorithm.HS256);

    // Token validity (in milliseconds), e.g. 1 hour
    private final long jwtExpirationMs = 3600000;

    /**
     * Generate a JWT token based on an Authentication object.
     * The token includes the username (subject) and a claim for roles.
     *
     * @param authentication the authentication object containing user details.
     * @return a signed JWT token.
     */
    public String generateToken(Authentication authentication) {
        String username = authentication.getName();
        List<String> roles = authentication.getAuthorities().stream()
                .map(Object::toString)
                .collect(Collectors.toList());
        return generateToken(username, roles);
    }

    /**
     * Generate a JWT token using a username and list of roles.
     *
     * @param username the username.
     * @param roles the list of roles.
     * @return a signed JWT token.
     */
    public String generateToken(String username, List<String> roles) {
        Date now = new Date();
        Date expiryDate = new Date(now.getTime() + jwtExpirationMs);

        return Jwts.builder()
                .setSubject(username)
                .claim("roles", roles)
                .setIssuedAt(now)
                .setExpiration(expiryDate)
                .signWith(secretKey, SignatureAlgorithm.HS256)
                .compact();
    }

    /**
     * Validate the JWT token.
     *
     * @param token the JWT token to validate.
     * @return true if the token is valid, false otherwise.
     */
    public boolean validateToken(String token) {
        try {
            Jwts.parserBuilder()
                    .setSigningKey(secretKey)
                    .build()
                    .parseClaimsJws(token);
            return true;
        } catch (JwtException | IllegalArgumentException ex) {
            // Optionally, log the error here for debugging.
            return false;
        }
    }

    /**
     * Extract the username (subject) from the JWT token.
     *
     * @param token the JWT token.
     * @return the username stored in the token.
     */
    public String getUsernameFromToken(String token) {
        Claims claims = Jwts.parserBuilder()
                .setSigningKey(secretKey)
                .build()
                .parseClaimsJws(token)
                .getBody();
        return claims.getSubject();
    }

    /**
     * Extract the roles from the JWT token.
     *
     * @param token the JWT token.
     * @return a list of roles stored as strings in the token.
     */
    @SuppressWarnings("unchecked")
    public List<String> getRolesFromToken(String token) {
        Claims claims = Jwts.parserBuilder()
                .setSigningKey(secretKey)
                .build()
                .parseClaimsJws(token)
                .getBody();
        return (List<String>) claims.get("roles");
    }
}
