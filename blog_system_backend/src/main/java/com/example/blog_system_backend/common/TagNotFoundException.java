package com.example.blog_system_backend.common;

public class TagNotFoundException extends RuntimeException {
    public TagNotFoundException(Long id) {
        super("tag not found: " + id);
    }

    public TagNotFoundException(String name) {
        super("tag not found: " + name);
    }
}
