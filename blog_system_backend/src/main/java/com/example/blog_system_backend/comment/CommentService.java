package com.example.blog_system_backend.comment;

import com.example.blog_system_backend.blog.Blog;
import com.example.blog_system_backend.comment.CommentLike;
import com.example.blog_system_backend.blog.BlogRepository;
import com.example.blog_system_backend.comment.dto.CommentRequest;
import com.example.blog_system_backend.comment.dto.CommentResponse;
import com.example.blog_system_backend.common.BlogNotFoundException;
import com.example.blog_system_backend.common.ChildCommentNotAllowedToAddCommentException;
import com.example.blog_system_backend.common.CommentNotFoundException;
import com.example.blog_system_backend.common.UserNotFoundException;
import com.example.blog_system_backend.user.User;
import com.example.blog_system_backend.user.UserRepository;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Optional;

@Service
public class CommentService {

    private final CommentRepository commentRepository;
    private final BlogRepository blogRepository;
    private final UserRepository userRepository;
    private final CommentLikeRepository commentLikeRepository;

    public CommentService(CommentRepository commentRepository, BlogRepository blogRepository, UserRepository userRepository, CommentLikeRepository commentLikeRepository) {
        this.commentRepository = commentRepository;
        this.blogRepository = blogRepository;
        this.userRepository = userRepository;
        this.commentLikeRepository = commentLikeRepository;
    }

    public Page<CommentResponse> getByBlogId(Long blogId, Pageable pageable) {
        Page<Comment> commentPage = commentRepository.findByBlogIdAndParentCommentNull(blogId, pageable);
        return commentPage.map(this::toResponseWithReplies);
    }

    // 创建评论(顶级评论)
    @Transactional
    public CommentResponse create(Long blogId, Long userId, CommentRequest request) {
        Blog blog = blogRepository.findById(blogId)
                .orElseThrow(() -> new BlogNotFoundException(blogId));

        User user = userRepository.findById(userId)
                .orElseThrow(() -> new UserNotFoundException(userId));

        Comment comment = new Comment();
        comment.setBlog(blog);
        comment.setUser(user);
        comment.setContent(request.content());
        comment.setParentComment(null);

        return toResponseWithReplies(commentRepository.save(comment));
    }

    @Transactional
    public CommentResponse replyComment(Long parentCommentId, Long userId, CommentRequest request) {
        // 找到要回复的父评论
        Comment parentComment = findById(parentCommentId);

        // 强制只能两级：父评论不能已经是子评论
        if (parentComment.getParentComment() != null) {
            throw new ChildCommentNotAllowedToAddCommentException(parentCommentId);
        }

        User user = userRepository.findById(userId)
                .orElseThrow(() -> new UserNotFoundException(userId));

        Comment comment = new Comment();
        // 回复归属的博客和父评论一致
        comment.setBlog(parentComment.getBlog());
        comment.setUser(user);
        comment.setContent(request.content());
        comment.setParentComment(parentComment);

        return toSimpleResponse(commentRepository.save(comment));
    }

    // 删除评论（自己/管理员可删）
    @Transactional
    public void delete(Long commentId, Long currentUserId, boolean isAdmin) {
        Comment comment = findById(commentId);

        boolean isOwner = comment.getUser().getId().equals(currentUserId);
        if (!isOwner && !isAdmin) {
            throw new IllegalArgumentException("No permission to delete this comment");
        }

        commentRepository.delete(comment);
    }


    private Comment findById(Long id) {
        return commentRepository.findById(id)
                .orElseThrow(() -> new CommentNotFoundException(id));
    }

    // 带回复的完整结构（给顶级评论用）
    private CommentResponse toResponseWithReplies(Comment comment) {
        List<Comment> replies = commentRepository.findByParentCommentId(comment.getId());
        List<CommentResponse> replyResponses = replies.stream()
                .map(this::toSimpleResponse)
                .toList();

        return buildResponse(comment, replyResponses);
    }

    // 简单结构（给子回复用，不再嵌套）
    private CommentResponse toSimpleResponse(Comment comment) {
        return buildResponse(comment, List.of());
    }

    // 点赞 / 取消点赞
    @Transactional
    public boolean toggleLike(Long commentId, Long userId) {
        Comment comment = findById(commentId);

        Optional<CommentLike> existingLike = commentLikeRepository.findByUserIdAndCommentId(userId, commentId);

        if (existingLike.isPresent()) {
            // 已点赞 → 取消点赞
            commentLikeRepository.delete(existingLike.get());
            comment.setLikeCount(comment.getLikeCount() - 1);
            commentRepository.save(comment);
            return false;
        } else {
            // 未点赞 → 点赞
            CommentLike commentLike = new CommentLike();
            commentLike.setUserId(userId);
            commentLike.setCommentId(commentId);
            commentLikeRepository.save(commentLike);

            comment.setLikeCount(comment.getLikeCount() + 1);
            commentRepository.save(comment);
            return true;
        }
    }

    // 查询当前用户是否点赞
    public boolean isLiked(Long commentId, Long userId) {
        findById(commentId);
        return commentLikeRepository.findByUserIdAndCommentId(userId, commentId).isPresent();
    }

    // 统一构建 DTO
    private CommentResponse buildResponse(Comment comment, List<CommentResponse> replies) {
        Long parentId = comment.getParentComment() != null ? comment.getParentComment().getId() : null;

        return new CommentResponse(
                comment.getId(),
                comment.getContent(),
                comment.getUser().getId(),
                comment.getUser().getUsername(),
                parentId,
                comment.getLikeCount(),
                comment.getCreateAt(),
                replies
        );
    }

}
