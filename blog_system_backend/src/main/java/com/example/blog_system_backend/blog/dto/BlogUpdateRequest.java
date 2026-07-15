package com.example.blog_system_backend.blog.dto;

import jakarta.validation.constraints.Size;

import java.util.Set;

public record BlogUpdateRequest(
        @Size(min = 2, max = 50, message = "blog title length must be between 2 and 50")
        String title,
        String content,
        Long categoryId,
        Set<Long> tagIds
) {
}
