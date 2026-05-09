package com.example.blog_system_backend.common;

public class BlogNotAllowedToDeleteException extends RuntimeException {
    public BlogNotAllowedToDeleteException(Long userId) {
        super("The user: " + userId + "doesn't have the right to delete this blog.");
    }
}
