package com.example.blog_system_backend.category.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;

public record CategoryCreateRequest(
        @NotBlank(message = "category name must not be blank")
        @Size(min = 1, max = 50, message = "category name length must be between 1 and 50")
        String name,
        @Size(max = 100, message = "分类描述最大100个字符")
        String description
) {
}
