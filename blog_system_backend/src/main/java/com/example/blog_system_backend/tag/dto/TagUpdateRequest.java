package com.example.blog_system_backend.tag.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;

public record TagUpdateRequest(
        @NotNull(message = "tag id must not be null")
        Long id,

        @NotBlank(message = "tag name must not be blank")
        @Size(min = 1, max = 50, message = "tag name length must be between 1 and 50")
        String name
) {
}
