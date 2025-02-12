package com.example.tkproject.controller;

import com.example.tkproject.exception.AuthenticationFailedException;
import com.example.tkproject.dto.payload.LoginRequest;
import com.example.tkproject.dto.payload.JwtResponse;
import com.example.tkproject.util.JwtUtil;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/auth")
public class AuthController {

    private static final Logger logger = LoggerFactory.getLogger(AuthController.class);

    private final AuthenticationManager authenticationManager;
    private final JwtUtil jwtTokenProvider;

    @Autowired
    public AuthController(AuthenticationManager authenticationManager, JwtUtil jwtTokenProvider) {
        this.authenticationManager = authenticationManager;
        this.jwtTokenProvider = jwtTokenProvider;
    }

    @PostMapping("/login")
    public ResponseEntity<JwtResponse> login(@RequestBody LoginRequest loginRequest) {
        logger.info("Login attempt for user: {}", loginRequest.getUsername());
        try {
            // Attempt to authenticate the user
            Authentication authentication = authenticationManager.authenticate(
                    new UsernamePasswordAuthenticationToken(
                            loginRequest.getUsername(), loginRequest.getPassword()
                    )
            );

            logger.info("User {} authenticated successfully", loginRequest.getUsername());

            // Generate a JWT token
            String token = jwtTokenProvider.generateToken(authentication);

            // Extract the first authority (role) from the authentication details
            String role = authentication.getAuthorities().stream()
                    .map(GrantedAuthority::getAuthority)
                    .findFirst()
                    .orElse("");

            logger.debug("Generated token for user {} with role {}", loginRequest.getUsername(), role);

            // Return the token and role in the response
            return ResponseEntity.ok(new JwtResponse(token, role));
        } catch (AuthenticationException ex) {
            logger.error("Authentication failed for user {}: {}", loginRequest.getUsername(), ex.getMessage());
            // Throw a custom exception; your global exception handler should catch this and return a 401 response.
            throw new AuthenticationFailedException("Invalid username or password", ex);
        }
    }
}
