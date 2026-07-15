package com.example.blog_system_backend.image;

import java.util.Map;

public record ImageUploadResponse(
        boolean status,
        String message,
        Map<String, Object> data
) {
}
