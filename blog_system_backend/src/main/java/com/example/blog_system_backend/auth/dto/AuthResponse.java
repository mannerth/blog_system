package com.example.blog_system_backend.auth.dto;

public record AuthResponse(
        String token,
        String tokenType,
        String username,
        String role
) {
}
