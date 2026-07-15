package com.example.blog_system_backend.user;

import com.example.blog_system_backend.blog.BlogService;
import com.example.blog_system_backend.blog.dto.BlogResponse;
import com.example.blog_system_backend.user.dto.UserProfileResponse;
import com.example.blog_system_backend.user.dto.UserCreateRequest;
import com.example.blog_system_backend.user.dto.UserUpdateRequest;
import jakarta.validation.Valid;

import java.util.List;
import java.lang.System;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/users")
public class UserController {

    private final UserService userService;
    private final BlogService blogService;

    public UserController(UserService userService, BlogService blogService) {
        this.userService = userService;
        this.blogService = blogService;
    }

    @GetMapping("/me")
    public UserProfileResponse me(Authentication authentication) {
        String username = authentication.getName();  // 直接获取用户名
        System.out.println(username);
        return userService.getByUsername(username);  // 用用户名查询
    }

    @GetMapping
    public List<UserProfileResponse> list() {
        return userService.getAll();
    }

    @GetMapping("/{id}")
    public UserProfileResponse getById(@PathVariable Long id) {
        return userService.getById(id);
    }

    @PostMapping
    public UserProfileResponse create(@Valid @RequestBody UserCreateRequest request) {
        return userService.create(request);
    }

    @PutMapping("/{id}")
    public UserProfileResponse update(@PathVariable Long id, @Valid @RequestBody UserUpdateRequest request) {
        return userService.update(id, request);
    }

    @DeleteMapping("/{id}")
    public void delete(@PathVariable Long id) {
        userService.delete(id);
    }

    @GetMapping("/me/blogs")
    public Page<BlogResponse> getMyBlogs(
            Authentication authentication,
            Pageable pageable) {

        String username = authentication.getName();

        User currentUser = userService.findUserByUsername(username);

        return blogService.getByUserId(currentUser.getId(), pageable);
    }

    @GetMapping("/{userId}/blogs")
    public Page<BlogResponse> getUserBlogs(
            @PathVariable Long userId,
            Pageable pageable) {
        return blogService.getByUserId(userId, pageable);
    }

}
