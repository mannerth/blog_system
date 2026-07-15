package com.example.blog_system_backend.comment.dto;

import java.time.LocalDateTime;
import java.util.List;

public record CommentResponse(
        Long id,
        String content,
        Long userId,
        String username,
        Long parentCommentId,
        Integer likeCount,
        LocalDateTime createdAt,
        List<CommentResponse> replies
) {
}
