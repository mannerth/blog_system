package com.example.blog_system_backend.user.dto;

import java.time.LocalDateTime;

public record UserProfileResponse(
        Long id,
        String username,
        String role,
        LocalDateTime createdAt
) {
}
