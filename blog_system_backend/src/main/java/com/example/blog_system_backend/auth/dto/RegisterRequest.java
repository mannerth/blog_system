package com.example.blog_system_backend.auth.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;

public record RegisterRequest(
        @NotBlank(message = "username must not be blank")
        @Size(min = 3, max = 50, message = "username length must be between 3 and 50")
        String username,
        @NotBlank(message = "password must not be blank")
        @Size(min = 6, max = 100, message = "password length must be between 6 and 100")
        String password
) {
}
