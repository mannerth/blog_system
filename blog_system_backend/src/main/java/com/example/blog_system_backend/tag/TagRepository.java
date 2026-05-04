package com.example.blog_system_backend.tag;

import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.Optional;
import java.util.Set;

public interface TagRepository extends JpaRepository<Tag, Long> {
    List<Tag> findAllByOrderByIdAsc();

    Optional<Tag> findByName(String name);

    boolean existsByName(String name);

    boolean existsByNameAndIdNot(String name, Long id);

    List<Tag> findAllByIdIn(Set<Long> tagIds);

    List<Tag> findAllByNameIn(Set<String> tagNames);
}
