package com.example.blog_system_backend.blog;

import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface BlogLikeRepository extends JpaRepository<BlogLike, Long> {
    // 查询用户是否已点赞该博客
    Optional<BlogLike> findByUserIdAndBlogId(Long userId, Long blogId);

    // 统计博客的点赞数
    long countByBlogId(Long blogId);
}
