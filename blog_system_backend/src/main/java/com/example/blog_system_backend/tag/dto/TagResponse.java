package com.example.blog_system_backend.tag.dto;

import java.time.LocalDateTime;

public record TagResponse(
        Long id,
        String name,
        LocalDateTime createAt
) {
}
