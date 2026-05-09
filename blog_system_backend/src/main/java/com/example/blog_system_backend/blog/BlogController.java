package com.example.blog_system_backend.blog;

import com.example.blog_system_backend.blog.dto.BlogCreateRequest;
import com.example.blog_system_backend.blog.dto.BlogResponse;
import com.example.blog_system_backend.blog.dto.BlogUpdateRequest;
import com.example.blog_system_backend.category.dto.CategoryCreateRequest;
import com.example.blog_system_backend.category.dto.CategoryResponse;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.web.bind.annotation.*;

import java.util.Collection;

@RestController
@RequestMapping("/api/blogs")
public class BlogController {

    private final BlogService blogService;

    public BlogController(BlogService blogService) { this.blogService = blogService; }

    @GetMapping("/{id}")
    public BlogResponse getById(@PathVariable Long id) {
        return blogService.getById(id);
    }

    @GetMapping
    public Page<BlogResponse> getAll(
            @RequestParam(required = false) Long categoryId,
            @RequestParam(required = false) Long tagId,
            @RequestParam(required = false) Collection<Long> tagIds,
            @RequestParam(required = false) String keyword,
            Pageable pageable
    ) {
        // 搜索
        if (keyword != null) {
            return blogService.search(keyword, pageable);
        }

        // 多标签查询
        if (tagIds != null && !tagIds.isEmpty()) {
            return blogService.getByTagIds(tagIds, pageable);
        }

        // 单标签查询
        if (tagId != null) {
            return blogService.getByTagId(tagId, pageable);
        }

        // 分类查询
        if (categoryId != null) {
            return blogService.getByCategoryId(categoryId, pageable);
        }

        // 默认查全部
        return blogService.getAll(pageable);
    }

    @GetMapping("/users/{userId}/blogs")
    public Page<BlogResponse> getByUserId(
            @PathVariable Long userId,
            Pageable pageable
    ) {
        return blogService.getByUserId(userId, pageable);
    }

    @PostMapping
    public BlogResponse create(
            @RequestBody BlogCreateRequest request,
            @RequestParam Long userId       //从URL获取用户ID
    ) {
        System.out.println("=====================> 拿到的 userId: " + userId);
        return blogService.create(request, userId);
    }

    @PutMapping("/{id}")
    public BlogResponse update(
            @PathVariable Long id,
            @RequestBody BlogUpdateRequest request,
            // 后续接入登录后换成从注解拿登录用户ID，现在开发可以先用@RequestParam临时测试
            @RequestParam Long loginUserId
    ) {
        return blogService.update(id, request, loginUserId);
    }

    @DeleteMapping("/{id}")
    public void delete(
            @PathVariable Long id,
            @RequestParam Long loginUserId
    ) {
        blogService.delete(id, loginUserId);
    }
}
