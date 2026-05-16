package com.example.blog_system_backend.admin;

import java.util.Map;

import com.example.blog_system_backend.blog.dto.BlogResponse;
import com.example.blog_system_backend.blog.dto.BlogUpdateRequest;
import com.example.blog_system_backend.comment.dto.CommentResponse;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/admin")
public class AdminController {

    private final AdminService adminService;

    public AdminController(AdminService adminBlogService) {
        this.adminService = adminBlogService;
    }

    @GetMapping("/ping")
    public Map<String, String> ping() {
        return Map.of("message", "admin access granted");
    }

    @GetMapping("/blogs")
    public Page<BlogResponse> getAllBlogs(Pageable pageable) {
        return adminService.getAllBlogs(pageable);
    }

    // PUT 管理员编辑任意博客
    @PutMapping("/blogs/{id}")
    public BlogResponse updateBlog(@PathVariable Long id, @RequestBody BlogUpdateRequest request) {
        return adminService.updateBlog(id, request);
    }

    // DELETE 管理员删除任意博客
    @DeleteMapping("/blogs/{id}")
    public void deleteBlog(@PathVariable Long id) {
        adminService.deleteBlog(id);
    }

    @GetMapping("/comments")
    public Page<CommentResponse> getAllComments(Pageable pageable) { return adminService.getAllComments(pageable); }

    @DeleteMapping("/comments/{id}")
    public void deleteComment(@PathVariable Long id) {
        adminService.deleteComment(id);
    }
}
