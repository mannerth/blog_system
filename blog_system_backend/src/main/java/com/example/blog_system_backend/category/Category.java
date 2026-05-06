package com.example.blog_system_backend.category;

import jakarta.persistence.*;

import java.time.LocalDateTime;

@Entity
@Table(name = "categories")
public class Category {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

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

    public Long getId() { return id; }

    public void setId(Long id) {
        this.id = id;
    }

    public String getName() { return name; }

    public void setName(String name) {
        this.name = name;
    }

    public LocalDateTime getCreateAt() { return createAt; }

    public String getDescription() { return description; }

    public void setDescription(String description) { this.description = description; }

    public void setCreateAt(LocalDateTime createAt) {
        this.createAt = createAt;
    }
}
