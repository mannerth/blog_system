package com.example.blog_system_backend.common;

public class CategoryNameAlreadyExistsException extends RuntimeException {

    public CategoryNameAlreadyExistsException(String name) {
        super("Category's name already exists: " + name);
    }
}
