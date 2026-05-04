package com.example.blog_system_backend.tag;

import com.example.blog_system_backend.tag.dto.TagCreateRequest;
import com.example.blog_system_backend.tag.dto.TagResponse;
import com.example.blog_system_backend.tag.dto.TagUpdateRequest;
import com.example.blog_system_backend.user.dto.UserUpdateRequest;
import jakarta.validation.Valid;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/tags")
public class TagController {

    private final TagService tagService;

    public TagController(TagService tagService) { this.tagService = tagService; }

    @GetMapping
    public List<TagResponse> list() { return tagService.getAll(); }

    @GetMapping("/{id}")
    public TagResponse getById(@PathVariable Long id) {
        return tagService.getById(id);
    }

    @PostMapping
    public TagResponse create(@RequestBody TagCreateRequest request) {
        return tagService.create(request);
    }

    @PutMapping("/{id}")
    public TagResponse update(@PathVariable Long id, @Valid @RequestBody TagUpdateRequest request) {
        return tagService.update(id, request);
    }

    @DeleteMapping("/{id}")
    public void delete(@PathVariable Long id) { tagService.delete(id); }

}
