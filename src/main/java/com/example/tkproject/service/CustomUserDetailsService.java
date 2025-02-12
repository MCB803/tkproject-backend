package com.example.tkproject.service;

import com.example.tkproject.model.User;
import com.example.tkproject.repository.UserRepository;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.*;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.stream.Collectors;

@Service
public class CustomUserDetailsService implements UserDetailsService {

    private static final Logger logger = LoggerFactory.getLogger(CustomUserDetailsService.class);

    private final UserRepository userRepository;

    // Inject your user repository (that is linked to your user and authorities tables)
    public CustomUserDetailsService(UserRepository userRepository) {
        this.userRepository = userRepository;
    }

    @Override
    public UserDetails loadUserByUsername(String username) throws UsernameNotFoundException {
        logger.debug("Attempting to load user by username: {}", username);

        User user = userRepository.findByUsername(username)
                .orElseThrow(() -> {
                    logger.warn("User not found: {}", username);
                    return new UsernameNotFoundException("User not found: " + username);
                });

        // Map your authorities (assuming user.getAuthorities() returns a collection of Authority)
        List<GrantedAuthority> grantedAuthorities = user.getAuthorities().stream()
                .map(auth -> {
                    String authority = auth.getAuthority();
                    logger.debug("Found authority: {}", authority);
                    return new SimpleGrantedAuthority(authority);
                })
                .collect(Collectors.toList());

        logger.info("User {} loaded successfully with {} authorities", username, grantedAuthorities.size());

        return new org.springframework.security.core.userdetails.User(
                user.getUsername(),
                user.getPassword(),
                grantedAuthorities
        );
    }
}
