package com.example.blog_system_backend.category.dto;

import org.springframework.cglib.core.Local;

import java.time.LocalDateTime;

public record CategoryResponse(
        Long id,
        String name,
        String description,
        LocalDateTime createAt
) {
}
