package com.example.blog_system_backend.image;

public record ImageTokenResponse(
        boolean status,
        String message,
        ImageTokenData data
) {
}
