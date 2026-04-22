package com.example.blog_system_backend.auth.dto;

import jakarta.validation.constraints.NotBlank;

public record LoginRequest(
        @NotBlank(message = "username must not be blank")
        String username,
        @NotBlank(message = "password must not be blank")
        String password
) {
}
