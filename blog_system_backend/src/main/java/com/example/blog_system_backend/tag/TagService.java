package com.example.blog_system_backend.tag;

import com.example.blog_system_backend.common.TagNameAlreadyExistsException;
import com.example.blog_system_backend.common.TagNotFoundException;
import com.example.blog_system_backend.tag.dto.TagCreateRequest;
import com.example.blog_system_backend.tag.dto.TagResponse;
import com.example.blog_system_backend.tag.dto.TagUpdateRequest;
import jakarta.transaction.Transactional;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class TagService {

    private final TagRepository tagRepository;

    public TagService(TagRepository tagRepository) {
        this.tagRepository = tagRepository;
    }

    public List<TagResponse> getAll() {
        return tagRepository.findAllByOrderByIdAsc().stream()
                .map(this::toResponse)
                .toList();
    }

    public TagResponse getById(Long id) {
        return toResponse(findTagById(id));
    }

    public TagResponse getByName(String name) {
        return toResponse(findTagByName(name));
    }

    @Transactional
    public TagResponse create(TagCreateRequest request) {
        String name = normalizeName(request.name());
        validateNameLength(name);

        if(tagRepository.existsByName(name)) {
            throw new TagNameAlreadyExistsException(name);
        }

        Tag tag = new Tag();
        tag.setName(name);

        return toResponse(tagRepository.save(tag));
    }

    @Transactional
    public TagResponse update(Long id, TagUpdateRequest request) {
        Tag tag = findTagById(id);
        String name = normalizeName(request.name());
        validateNameLength(name);

        if(tagRepository.existsByNameAndIdNot(name, id)) {
            throw new TagNameAlreadyExistsException(name);
        }

        tag.setName(name);

        return toResponse(tagRepository.save(tag));
    }

    @Transactional
    public void delete(Long id) {
        if (!tagRepository.existsById(id)) {
            throw new TagNotFoundException(id);
        }
        tagRepository.deleteById(id);
    }

    private Tag findTagById(Long id) {
        return tagRepository.findById(id)
                .orElseThrow(() -> new TagNotFoundException(id));
    }

    private Tag findTagByName(String name) {
        String normalizedName = normalizeName(name);
        return tagRepository.findByName(normalizedName)
                .orElseThrow(() -> new TagNotFoundException(normalizedName));
    }

    private String normalizeName(String name) {
        if (name == null) {
            throw new IllegalArgumentException("Tag's name must not be blank");
        }
        return name.trim();
    }

    private void validateNameLength(String name) {
        if (name.isBlank()) {
            throw new IllegalArgumentException("Tag's name must not be blank");
        }
        if (name.length() < 3 || name.length() > 50) {
            throw new IllegalArgumentException("Tag's name length must be between 3 and 50");
        }
    }

    private TagResponse toResponse(Tag tag) {
        return new TagResponse(
                tag.getId(),
                tag.getName(),
                tag.getCreateAt()
        );
    }
}
