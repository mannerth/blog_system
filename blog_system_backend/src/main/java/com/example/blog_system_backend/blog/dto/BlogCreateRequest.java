package com.example.blog_system_backend.blog.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;

import java.util.Set;

public record BlogCreateRequest(
        @NotBlank(message = "blog title must not be blank")
        @Size(min = 2, max = 50, message = "blog title length must be between 2 and 50")
        String title,
        @NotBlank(message = "blog content must not be blank")
        String content,
        @NotNull(message = "blog must belong to a category")
        Long categoryId,
        Set<Long> tagIds
) {
}
