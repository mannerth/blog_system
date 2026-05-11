package com.example.blog_system_backend.blog;

import com.example.blog_system_backend.blog.dto.BlogCreateRequest;
import com.example.blog_system_backend.blog.dto.BlogResponse;
import com.example.blog_system_backend.blog.dto.BlogUpdateRequest;
import com.example.blog_system_backend.user.User;
import com.example.blog_system_backend.user.UserService;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;

import java.util.Collection;

@RestController
@RequestMapping("/api/blogs")
public class BlogController {

    private final BlogService blogService;
    private final UserService userService;

    public BlogController(BlogService blogService, UserService userService) {
        this.blogService = blogService;
        this.userService = userService;
    }

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
            Authentication authentication
    ) {
        String username = authentication.getName();

        User currentUser = userService.findUserByUsername(username);
        System.out.println("=====================> 拿到的 userId: " + currentUser.getId());
        return blogService.create(request, currentUser.getId());
    }

    @PutMapping("/{id}")
    public BlogResponse update(
            @PathVariable Long id,
            @RequestBody BlogUpdateRequest request,
            Authentication authentication
    ) {
        String username = authentication.getName();

        User currentUser = userService.findUserByUsername(username);
        return blogService.update(id, request, currentUser.getId());
    }

    @DeleteMapping("/{id}")
    public void delete(
            @PathVariable Long id,
            Authentication authentication
    ) {
        String username = authentication.getName();

        User currentUser = userService.findUserByUsername(username);
        blogService.delete(id, currentUser.getId());
    }
}
