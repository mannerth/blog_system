package com.example.blog_system_backend.comment;

import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Collection;
import java.util.Optional;

public interface CommentLikeRepository extends JpaRepository<CommentLike, Long> {
    // 查询用户是否已点赞该评论
    Optional<CommentLike> findByUserIdAndCommentId(Long userId, Long commentId);

    void deleteByCommentIdIn(Collection<Long> commentIds);

    // 统计评论的点赞数
    long countByCommentId(Long commentId);
}

