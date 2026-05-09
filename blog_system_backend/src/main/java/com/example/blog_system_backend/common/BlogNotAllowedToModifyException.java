package com.example.blog_system_backend.common;

public class BlogNotAllowedToModifyException extends RuntimeException {
    public BlogNotAllowedToModifyException(Long userId) {
        super("The user: " + userId + "doesn't have the right to modify this blog.");
    }
}
