package com.example.blog_system_backend.category;

import jakarta.persistence.*;

import java.time.LocalDateTime;

@Entity
@Table(name = "categories")
public class Category {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long category_id;

    @Column(nullable = false, unique = true, length = 50)
    private String name;

    @Column(nullable = true, length = 100)
    private String description;

    @Column(nullable = false, updatable = false)
    private LocalDateTime createAt;
    @PrePersist
    public void prePersist() {
        this.createAt = LocalDateTime.now();
    }
}
