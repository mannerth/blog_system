package com.example.blog_system_backend.blog;

import com.example.blog_system_backend.blog.dto.BlogCreateRequest;
import com.example.blog_system_backend.blog.dto.BlogResponse;
import com.example.blog_system_backend.blog.dto.BlogUpdateRequest;
import com.example.blog_system_backend.category.Category;
import com.example.blog_system_backend.category.CategoryRepository;
import com.example.blog_system_backend.comment.Comment;
import com.example.blog_system_backend.comment.CommentLikeRepository;
import com.example.blog_system_backend.comment.CommentRepository;
import com.example.blog_system_backend.common.BlogNotAllowedToModifyException;
import com.example.blog_system_backend.common.CategoryNotFoundException;
import com.example.blog_system_backend.tag.Tag;
import com.example.blog_system_backend.tag.TagRepository;
import com.example.blog_system_backend.user.User;
import com.example.blog_system_backend.user.UserRepository;
import jakarta.persistence.EntityNotFoundException;
import jakarta.persistence.criteria.Predicate;

import java.util.*;
import java.util.stream.Collectors;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.*;
import java.util.stream.Collectors;

@Service
public class BlogService {

    private final BlogRepository blogRepository;
    private final CategoryRepository categoryRepository;
    private final TagRepository tagRepository;
    private final UserRepository userRepository;
    private final BlogLikeRepository blogLikeRepository;
    private final CommentRepository commentRepository;
    private final CommentLikeRepository commentLikeRepository;

    public BlogService(BlogRepository blogRepository, CategoryRepository categoryRepository, TagRepository tagRepository, UserRepository userRepository, BlogLikeRepository blogLikeRepository, CommentRepository commentRepository, CommentLikeRepository commentLikeRepository) {
        this.blogRepository = blogRepository;
        this.categoryRepository = categoryRepository;
        this.tagRepository = tagRepository;
        this.userRepository = userRepository;
        this.blogLikeRepository = blogLikeRepository;
        this.commentRepository = commentRepository;
        this.commentLikeRepository = commentLikeRepository;
    }

    public BlogResponse getById(Long id) {
        return toResponse(findById(id));
    }

    public Page<BlogResponse> findAll(Long categoryId, Collection<Long> tagIds, String keyword, Pageable pageable) {
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

    // ===================== 分页查询所有博客（首页） =====================
    public Page<BlogResponse> getAll(Pageable pageable) {
        return blogRepository.findAll(pageable).map(this::toResponse);
    }

    // ===================== 根据用户ID查询博客 =====================
    public Page<BlogResponse> getByUserId(Long userId, Pageable pageable) {
        return blogRepository.findByUserId(userId, pageable).map(this::toResponse);
    }

    // ===================== 根据分类ID查询博客 =====================
    public Page<BlogResponse> getByCategoryId(Long categoryId, Pageable pageable) {
        return blogRepository.findByCategoryId(categoryId, pageable).map(this::toResponse);
    }

    // ===================== 根据标签ID查询博客 =====================
    public Page<BlogResponse> getByTagId(Long tagId, Pageable pageable) {
        return blogRepository.findByTagsId(tagId, pageable).map(this::toResponse);
    }

    public Page<BlogResponse> getByTagIds(Collection<Long> tagIds, Pageable pageable) {
        return blogRepository.findByTagsIdIn(tagIds, pageable).map(this::toResponse);
    }

    public Page<BlogResponse> search(String keyword, Pageable pageable) {
        return blogRepository.findByTitleContainingOrContentContaining(keyword, keyword, pageable)
                .map(this::toResponse);
    }

    public BlogResponse create(BlogCreateRequest request, Long userId) {
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new EntityNotFoundException("用户不存在"));

        Category category = categoryRepository.findById(request.categoryId())
                .orElseThrow(() -> new EntityNotFoundException("分类不存在"));

        Set<Tag> tags = new HashSet<>();
        if (request.tagIds() != null && !request.tagIds().isEmpty()) {
            tags = new HashSet<>(tagRepository.findAllById(request.tagIds()));
        }

        Blog blog = new Blog();
        blog.setTitle(request.title());
        blog.setContent(request.content());
        blog.setUser(user);
        blog.setCategory(category);
        blog.setTags(tags);

        return toResponse(blogRepository.save(blog));
    }

    public BlogResponse update(Long id, BlogUpdateRequest request, Long loginUserId) {
        Blog blog = findById(id);

        if (!blog.getUser().getId().equals(loginUserId)) {
            throw new BlogNotAllowedToModifyException(loginUserId);
        }

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

    @Transactional
    public void delete(Long id, Long loginUserId) {
        Blog blog = findById(id);
        if (!blog.getUser().getId().equals(loginUserId)) {
            throw new BlogNotAllowedToModifyException(loginUserId);
        }
        deleteBlogRelations(id);
        blogRepository.deleteById(id);
    }

    public void deleteBlogRelations(Long blogId) {
        // 1. 获取所有评论
        List<Comment> comments = commentRepository.findIdsByBlogId(blogId);

        // 2. 提取评论ID
        List<Long> commentIds = comments.stream()
                .map(Comment::getId)
                .toList();

        // 3. 删除评论点赞
        if (!commentIds.isEmpty()) {
            commentLikeRepository.deleteByCommentIdIn(commentIds);
        }

        // 4. 删除评论
        commentRepository.deleteByBlogId(blogId);

        // 5. 删除博客点赞
        blogLikeRepository.deleteByBlogId(blogId);
    }

    // ===================== 点赞 / 取消点赞 =====================
    @Transactional
    public boolean toggleLike(Long blogId, Long userId) {
        Blog blog = findById(blogId);

        Optional<BlogLike> existingLike = blogLikeRepository.findByUserIdAndBlogId(userId, blogId);

        if (existingLike.isPresent()) {
            // 已点赞 → 取消点赞
            blogLikeRepository.delete(existingLike.get());
            blog.setLikeCount(blog.getLikeCount() - 1);
            blogRepository.save(blog);
            return false;
        } else {
            // 未点赞 → 点赞
            BlogLike blogLike = new BlogLike();
            blogLike.setUserId(userId);
            blogLike.setBlogId(blogId);
            blogLikeRepository.save(blogLike);

            blog.setLikeCount(blog.getLikeCount() + 1);
            blogRepository.save(blog);
            return true;
        }
    }

    @Transactional
    public int incrementViewCount(Long id) {
        Blog blog = findById(id);
        blog.setViewCount(blog.getViewCount() + 1);
        blogRepository.save(blog);
        return blog.getViewCount();
    }

    public boolean isLiked(Long blogId, Long userId) {
        findById(blogId);
        return blogLikeRepository.findByUserIdAndBlogId(userId, blogId).isPresent();
    }

    private Blog findById(Long id) {
        return blogRepository.findById(id)
                .orElseThrow(() -> new EntityNotFoundException("Blog not exists, id:" + id));
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
                blog.getTags().stream()
                                .map(tag -> tag.getName())
                                .collect(Collectors.toSet()),
                blog.getViewCount(),
                blog.getLikeCount(),
                blog.getCreateAt(),
                blog.getUpdatedAt()
        );
    }
}
