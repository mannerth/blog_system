package com.example.blog_system_backend.admin;

import com.example.blog_system_backend.blog.Blog;
import com.example.blog_system_backend.blog.BlogRepository;
import com.example.blog_system_backend.blog.BlogService;
import com.example.blog_system_backend.blog.dto.BlogResponse;
import com.example.blog_system_backend.blog.dto.BlogUpdateRequest;
import com.example.blog_system_backend.category.Category;
import com.example.blog_system_backend.category.CategoryRepository;
import com.example.blog_system_backend.comment.Comment;
import com.example.blog_system_backend.comment.CommentLikeRepository;
import com.example.blog_system_backend.comment.CommentRepository;
import com.example.blog_system_backend.comment.CommentService;
import com.example.blog_system_backend.comment.dto.CommentResponse;
import com.example.blog_system_backend.common.BlogNotFoundException;
import com.example.blog_system_backend.common.CategoryNotFoundException;
import com.example.blog_system_backend.common.CommentNotFoundException;
import com.example.blog_system_backend.tag.Tag;
import com.example.blog_system_backend.tag.TagRepository;
import jakarta.persistence.EntityNotFoundException;
import jakarta.persistence.criteria.Predicate;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

@Service
public class AdminService {

    private final BlogRepository blogRepository;
    private final CategoryRepository categoryRepository;
    private final TagRepository tagRepository;
    private final CommentRepository commentRepository;
    private final CommentLikeRepository commentLikeRepository;
    private final BlogService blogService;


    public AdminService(BlogRepository blogRepository,
                        CategoryRepository categoryRepository,
                        TagRepository tagRepository,
                        CommentRepository commentRepository,
                        CommentLikeRepository commentLikeRepository,
                        BlogService blogService
) {
        this.blogRepository = blogRepository;
        this.categoryRepository = categoryRepository;
        this.tagRepository = tagRepository;
        this.commentRepository = commentRepository;
        this.commentLikeRepository = commentLikeRepository;
        this.blogService = blogService;
    }

    // 获取全站博客列表（管理员无权限限制，支持组合筛选）
    public Page<BlogResponse> getAllBlogs(Long categoryId, Collection<Long> tagIds, String keyword, Pageable pageable) {
        Specification<Blog> spec = (root, query, cb) -> {
            List<Predicate> predicates = new ArrayList<>();

            if (keyword != null && !keyword.isBlank()) {
                String pattern = "%" + keyword + "%";
                predicates.add(cb.or(
                        cb.like(root.get("title"), pattern),
                        cb.like(root.get("content"), pattern)
                ));
            }

            if (categoryId != null) {
                predicates.add(cb.equal(root.get("category").get("id"), categoryId));
            }

            if (tagIds != null && !tagIds.isEmpty()) {
                predicates.add(root.join("tags").get("id").in(tagIds));
            }

            return cb.and(predicates.toArray(new Predicate[0]));
        };

        return blogRepository.findAll(spec, pageable).map(this::toResponse);
    }

    // 管理员编辑任意博客（跳过作者权限校验）
    @Transactional
    public BlogResponse updateBlog(Long id, BlogUpdateRequest request) {
        Blog blog = findById(id);

        if (request.title() != null) {
            blog.setTitle(request.title());
        }
        if (request.content() != null) {
            blog.setContent(request.content());
        }
        if (request.categoryId() != null) {
            Category category = categoryRepository.findById(request.categoryId())
                    .orElseThrow(() -> new CategoryNotFoundException(request.categoryId()));
            blog.setCategory(category);
        }
        if (request.tagIds() != null) {
            Set<Tag> tags = tagRepository.findAllById(request.tagIds()).stream().collect(Collectors.toSet());
            blog.setTags(tags);
        }

        return toResponse(blogRepository.save(blog));
    }

    // 管理员删除任意博客（跳过作者权限校验）
    @Transactional
    public void deleteBlog(Long id) {
        if (!blogRepository.existsById(id)) {
            throw new BlogNotFoundException(id);
        }
        blogService.deleteBlogRelations(id);
        blogRepository.deleteById(id);
    }

    private Blog findById(Long id) {
        return blogRepository.findById(id)
                .orElseThrow(() -> new EntityNotFoundException("Blog not exists, id:" + id));
    }

    public Page<CommentResponse> getAllComments(Pageable pageable) {
        return commentRepository.findAll(pageable).map(this::toResponse);
    }

    @Transactional
    public void deleteComment(Long id) {
        // 1. 检查评论是否存在
        Comment comment = commentRepository.findById(id)
                .orElseThrow(() -> new CommentNotFoundException(id));

        // 2. 找出所有子评论（一层）
        List<Comment> childComments = commentRepository.findByParentCommentId(comment.getId());

        // 3. 收集所有要删除的评论ID（主评论 + 子评论）
        List<Long> allCommentIds = new ArrayList<>();
        allCommentIds.add(comment.getId());
        allCommentIds.addAll(childComments.stream()
                .map(Comment::getId)
                .toList());

        // 4. 删除所有点赞
        commentLikeRepository.deleteByCommentIdIn(allCommentIds);

        // 5. 删除子评论
        commentRepository.deleteAll(childComments);

        // 6. 删除主评论
        commentRepository.delete(comment);
    }
    
    private BlogResponse toResponse(Blog blog) {
        return new BlogResponse(
                blog.getId(),
                blog.getTitle(),
                blog.getContent(),
                blog.getUser().getId(),
                blog.getUser().getUsername(),
                blog.getCategory().getId(),
                blog.getCategory().getName(),
                blog.getTags().stream().map(Tag::getName).collect(Collectors.toSet()),
                blog.getViewCount(),
                blog.getLikeCount(),
                blog.getCreateAt(),
                blog.getUpdatedAt()
        );
    }

    private CommentResponse toResponse(Comment comment) {
        return new CommentResponse(
                comment.getId(),
                comment.getContent(),
                comment.getUser().getId(),
                comment.getUser().getUsername(),
                // 父评论ID
                comment.getParentComment() != null ? comment.getParentComment().getId() : null,
                comment.getLikeCount(),
                comment.getCreateAt(),
                // 管理员列表不需要嵌套回复，空列表即可
                List.of()
        );
    }
}
