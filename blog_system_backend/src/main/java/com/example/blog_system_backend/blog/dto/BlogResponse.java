package com.example.blog_system_backend.blog.dto;

import java.time.LocalDateTime;
import java.util.Set;

public record BlogResponse(
        Long id,
        String title,
        String content,

        Long userId,
        String username,

        Long categoryId,
        String categoryName,

        Set<String> tagNames,
        Integer viewCount,
        Integer likeCount,
        LocalDateTime createAt,
        LocalDateTime updatedAt
) {
}
