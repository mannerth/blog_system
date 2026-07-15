package com.example.blog_system_backend.blog;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;

import java.util.Collection;

public interface BlogRepository extends JpaRepository<Blog, Long>, JpaSpecificationExecutor<Blog> {

    // 1. 按用户ID分页查询博客（/users/me/blogs 和 /users/{user_id}/blogs 共用）
    Page<Blog> findByUserId(Long userId, Pageable pageable);

    // 2. 按标题/内容模糊搜索 + 分页（对应 /blogs 的搜索功能）
    Page<Blog> findByTitleContainingOrContentContaining(
            String titleKeyword,
            String contentKeyword,
            Pageable pageable
    );

    // 3. 按分类ID过滤 + 分页（对应 /blogs 的过滤功能）
    Page<Blog> findByCategoryId(Long categoryId, Pageable pageable);

    // 4. 按用户ID + 分类ID过滤
    Page<Blog> findByUserIdAndCategoryId(Long userId, Long categoryId, Pageable pageable);

    // 单标签查询
    Page<Blog> findByTagsId(Long tagId, Pageable pageable);

    // 多标签查询
    Page<Blog> findByTagsIdIn(Collection<Long> tagIds, Pageable pageable);
}
