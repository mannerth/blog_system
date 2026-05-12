package com.example.blog_system_backend.blog;

import com.example.blog_system_backend.blog.dto.BlogCreateRequest;
import com.example.blog_system_backend.blog.dto.BlogResponse;
import com.example.blog_system_backend.blog.dto.BlogUpdateRequest;
import com.example.blog_system_backend.comment.CommentService;
import com.example.blog_system_backend.comment.dto.CommentRequest;
import com.example.blog_system_backend.comment.dto.CommentResponse;
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
    private final CommentService commentService;

    public BlogController(BlogService blogService, UserService userService, CommentService commentService) {
        this.blogService = blogService;
        this.userService = userService;
        this.commentService = commentService;
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

    // 获取某博客下的所有评论
    @GetMapping("/{blogId}/comments")
    public Page<CommentResponse> getCommentsByBlogId(
            @PathVariable Long blogId,
            Pageable pageable
    ) {
        return commentService.getByBlogId(blogId, pageable);
    }

    // 发表顶级评论
    @PostMapping("/{blogId}/comments")
    public CommentResponse create(
            @PathVariable Long blogId,
            @RequestBody CommentRequest request,
            Authentication authentication
    ) {
        String username = authentication.getName();
        User currentUser = userService.findUserByUsername(username);

        System.out.println("=====================> 评论用户 ID: " + currentUser.getId());
        return commentService.create(blogId, currentUser.getId(), request);
    }
}
