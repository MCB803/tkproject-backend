package com.example.tkproject.dto.payload;

import lombok.Data;

@Data
public class LoginRequest {
    private String username;
    private String password;
}
