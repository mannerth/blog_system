package com.example.blog_system_backend.common;

public class UsernameAlreadyExistsException extends RuntimeException {

    public UsernameAlreadyExistsException(String username) {
        super("username already exists: " + username);
    }
}

