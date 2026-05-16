package com.example.blog_system_backend.admin;

import com.example.blog_system_backend.blog.Blog;
import com.example.blog_system_backend.blog.BlogRepository;
import com.example.blog_system_backend.blog.BlogService;
import com.example.blog_system_backend.blog.dto.BlogResponse;
import com.example.blog_system_backend.blog.dto.BlogUpdateRequest;
import com.example.blog_system_backend.category.Category;
import com.example.blog_system_backend.category.CategoryRepository;
import com.example.blog_system_backend.common.BlogNotFoundException;
import com.example.blog_system_backend.common.CategoryNotFoundException;
import com.example.blog_system_backend.tag.Tag;
import com.example.blog_system_backend.tag.TagRepository;
import jakarta.persistence.EntityNotFoundException;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.Set;
import java.util.stream.Collectors;

@Service
public class AdminService {

    private final BlogRepository blogRepository;
    private final CategoryRepository categoryRepository;
    private final TagRepository tagRepository;
    private final BlogService blogService;

    public AdminService(BlogRepository blogRepository,
                        CategoryRepository categoryRepository,
                        TagRepository tagRepository,
                        BlogService blogService
) {
        this.blogRepository = blogRepository;
        this.categoryRepository = categoryRepository;
        this.tagRepository = tagRepository;
        this.blogService = blogService;
    }

    // 获取全站博客列表（管理员无权限限制）
    public Page<BlogResponse> getAllBlogs(Pageable pageable) {
        return blogRepository.findAll(pageable).map(this::toResponse);
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

    // 复用你项目已有的 BlogResponse 结构
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
}
