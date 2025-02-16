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

import java.util.List;
import java.util.stream.Collectors;

@RestController
@RequestMapping("/api/auth")
public class AuthController {

    private static final Logger logger = LoggerFactory.getLogger(AuthController.class);

    private final AuthenticationManager authenticationManager;
    private final JwtUtil jwtUtil;

    @Autowired
    public AuthController(AuthenticationManager authenticationManager, JwtUtil jwtUtil) {
        this.authenticationManager = authenticationManager;
        this.jwtUtil = jwtUtil;
    }

    @PostMapping("/login")
    public ResponseEntity<JwtResponse> login(@RequestBody LoginRequest loginRequest) {
        logger.info("Login attempt for user: {}", loginRequest.getUsername());
        try {
            Authentication authentication = authenticationManager.authenticate(
                    new UsernamePasswordAuthenticationToken(loginRequest.getUsername(), loginRequest.getPassword())
            );

            logger.info("User {} authenticated successfully", loginRequest.getUsername());

            String token = jwtUtil.generateToken(authentication);

            List<String> roles = authentication.getAuthorities().stream()
                    .map(GrantedAuthority::getAuthority)
                    .collect(Collectors.toList());

            String primaryRole = roles.isEmpty() ? "NO_ROLE" : roles.getFirst();
            logger.debug("Generated token for user {} with role {}", loginRequest.getUsername(), primaryRole);

            return ResponseEntity.ok(new JwtResponse(token, primaryRole));
        } catch (AuthenticationException ex) {
            logger.error("Authentication failed for user {}: {}", loginRequest.getUsername(), ex.getMessage());
            throw new AuthenticationFailedException("Invalid username or password", ex);
        }
    }
}
