package com.example.blog_system_backend.common;

public class BlogNotFoundException extends RuntimeException {
    public BlogNotFoundException(Long id) {
        super("blog not found: " + id);
    }

    public BlogNotFoundException(String title) {
        super("blog not found: " + title);
    }
}

