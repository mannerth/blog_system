package com.example.blog_system_backend.comment;

import com.example.blog_system_backend.comment.dto.CommentRequest;
import com.example.blog_system_backend.comment.dto.CommentResponse;
import com.example.blog_system_backend.user.User;
import com.example.blog_system_backend.user.UserService;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/comments")
public class CommentController {

    private final CommentService commentService;
    private final UserService userService;

    // 构造器注入
    public CommentController(CommentService commentService, UserService userService) {
        this.commentService = commentService;
        this.userService = userService;
    }

    // 在某条评论下创建回复
    @PostMapping("/{commentId}/replies")
    public CommentResponse replyComment(
            @PathVariable Long commentId,
            @RequestBody CommentRequest request,
            Authentication authentication
    ) {
        String username = authentication.getName();
        User currentUser = userService.findUserByUsername(username);
        return commentService.replyComment(commentId, currentUser.getId(), request);
    }

    // 删除评论
    @DeleteMapping("/{commentId}")
    public void delete(
            @PathVariable Long commentId,
            Authentication authentication
    ) {
        String username = authentication.getName();
        User currentUser = userService.findUserByUsername(username);

        boolean isAdmin = authentication.getAuthorities().stream()
                .anyMatch(granted -> granted.getAuthority().equals("ROLE_ADMIN"));

        commentService.delete(commentId, currentUser.getId(), isAdmin);
    }
}
