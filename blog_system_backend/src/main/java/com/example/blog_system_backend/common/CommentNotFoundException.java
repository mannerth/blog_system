package com.example.blog_system_backend.common;

public class CommentNotFoundException extends RuntimeException {
    public CommentNotFoundException(Long id) {
        super("comment not found: " + id);
    }

    public CommentNotFoundException(String name) {
        super("comment not found: " + name);
    }
}
