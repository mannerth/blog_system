package com.example.blog_system_backend.comment;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface CommentRepository extends JpaRepository<Comment, Long> {
    Page<Comment> findByBlogIdAndParentCommentNull(Long blogId, Pageable pageable);

    List<Comment> findByParentCommentId(Long parentCommentId);

    void deleteByBlogId(Long blogId);
}
