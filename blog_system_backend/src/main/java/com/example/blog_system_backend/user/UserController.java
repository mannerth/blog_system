package com.example.blog_system_backend.user;

import com.example.blog_system_backend.user.dto.UserProfileResponse;
import com.example.blog_system_backend.user.dto.UserCreateRequest;
import com.example.blog_system_backend.user.dto.UserUpdateRequest;
import jakarta.validation.Valid;
import java.util.List;
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

    public UserController(UserService userService) {
        this.userService = userService;
    }

    @GetMapping("/me")
    public UserProfileResponse me(Authentication authentication) {
        return userService.getByUsername(authentication.getName());
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
}
