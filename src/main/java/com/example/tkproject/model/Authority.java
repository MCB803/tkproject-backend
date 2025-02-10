package com.example.tkproject.model;

import jakarta.persistence.*;
import lombok.Data;
import org.springframework.security.core.GrantedAuthority;

@Entity
@Table(name = "authorities", schema = "security")
@Data
public class Authority implements GrantedAuthority {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    // The authority name (for example, ROLE_ADMIN, ROLE_AGENCY)
    @Column(nullable = false, length = 50)
    private String authority;

    @Override
    public String getAuthority() {
        return authority;
    }
}
