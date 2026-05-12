package com.example.blog_system_backend.common;

public class ChildCommentNotAllowedToAddCommentException extends RuntimeException {
    public ChildCommentNotAllowedToAddCommentException(Long id) {
        super("child comment: " + id + " not allowed to add comment");
    }
}
