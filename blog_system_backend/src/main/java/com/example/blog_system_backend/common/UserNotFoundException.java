package com.example.blog_system_backend.common;

public class UserNotFoundException extends RuntimeException {

    public UserNotFoundException(Long id) {
        super("user not found: " + id);
    }

    public UserNotFoundException(String username) {
        super("user not found: " + username);
    }
}

