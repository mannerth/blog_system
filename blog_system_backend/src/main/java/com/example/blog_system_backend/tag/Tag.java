package com.example.blog_system_backend.tag;

import com.example.blog_system_backend.blog.Blog;
import jakarta.persistence.*;
import org.springframework.cglib.core.Local;

import java.time.LocalDateTime;
import java.util.Set;

@Entity
@Table(name = "tags")
public class Tag {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false, unique = true, length = 50)
    private String name;

    @Column(nullable = false, updatable = false)
    private LocalDateTime createAt;

    @ManyToMany(mappedBy = "tags")
    private Set<Blog> blogs;

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

    public void setCreateAt(LocalDateTime createAt) {
        this.createAt = createAt;
    }
}
