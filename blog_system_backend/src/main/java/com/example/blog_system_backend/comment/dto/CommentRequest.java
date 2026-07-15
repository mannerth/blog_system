package com.example.blog_system_backend.comment.dto;

import jakarta.validation.constraints.NotBlank;

public record CommentRequest(
        @NotBlank(message = "comment must not be blank")
        String content
) {
}
