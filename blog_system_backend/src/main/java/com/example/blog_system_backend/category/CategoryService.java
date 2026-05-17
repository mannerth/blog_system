package com.example.blog_system_backend.category;

import com.example.blog_system_backend.category.dto.CategoryCreateRequest;
import com.example.blog_system_backend.category.dto.CategoryResponse;
import com.example.blog_system_backend.category.dto.CategoryUpdateRequest;
import com.example.blog_system_backend.common.CategoryNameAlreadyExistsException;
import com.example.blog_system_backend.common.CategoryNotFoundException;
import jakarta.transaction.Transactional;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class CategoryService {

    private final CategoryRepository categoryRepository;

    public CategoryService(CategoryRepository categoryRepository) { this.categoryRepository = categoryRepository; }

    public List<CategoryResponse> getAll() {
        return categoryRepository.findAllByOrderByIdAsc().stream()
                .map(this::toResponse)
                .toList();
    }

    public CategoryResponse getById(Long id) {
        return toResponse(findCategoryById(id));
    }

    public CategoryResponse getByName(String name) {
        return toResponse(findCategoryByName(name));
    }

    @Transactional
    public CategoryResponse create(CategoryCreateRequest request) {
        String name = normalizeName(request.name());
        validateNameLength(name);

        if(categoryRepository.existsByName(name)) {
            throw new CategoryNameAlreadyExistsException(name);
        }

        Category category = new Category();
        category.setName(name);
        category.setDescription(request.description());

        return toResponse(categoryRepository.save(category));
    }

    @Transactional
    public CategoryResponse update(Long id, CategoryUpdateRequest request) {
        Category category = findCategoryById(id);

        if(request.name() != null) {
            String name = normalizeName(request.name());
            validateNameLength(name);

            if(categoryRepository.existsByNameAndIdNot(name, id)) {
                throw new CategoryNameAlreadyExistsException(name);
            }
            category.setName(name);
        }

        if(request.description() != null) {
            category.setDescription(request.description());
        }

        return toResponse(categoryRepository.save(category));
    }

    @Transactional
    public void delete(Long id) {
        if(!categoryRepository.existsById(id)) {
            throw new CategoryNotFoundException(id);
        }
        categoryRepository.deleteById(id);
    }

    private Category findCategoryById(Long id) {
        return categoryRepository.findById(id)
                .orElseThrow(() -> new CategoryNotFoundException(id));
    }

    private Category findCategoryByName(String name) {
        String normalizedName = normalizeName(name);
        return categoryRepository.findByName(normalizedName)
                .orElseThrow(() -> new CategoryNotFoundException(normalizedName));
    }

    private String normalizeName(String name) {
        if (name == null) {
            throw new IllegalArgumentException("Category's name must not be blank");
        }
        return name.trim();
    }

    private void validateNameLength(String name) {
        if (name.isBlank()) {
            throw new IllegalArgumentException("Category's name must not be blank");
        }
        if ( name.length() > 50 ) {
            throw new IllegalArgumentException("Category's name length must less than 50");
        }
    }

    private CategoryResponse toResponse(Category category) {
        return new CategoryResponse(
                category.getId(),
                category.getName(),
                category.getDescription(),
                category.getCreateAt()
        );
    }
}
