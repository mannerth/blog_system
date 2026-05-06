package com.example.blog_system_backend.common;

public class CategoryNotFoundException extends RuntimeException {
    public CategoryNotFoundException(Long id) {
        super("category not found: " + id);
    }

    public CategoryNotFoundException(String name) {
        super("category not found: " + name);
    }
}