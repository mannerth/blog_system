package com.example.blog_system_backend.user;

import com.example.blog_system_backend.common.UserNotFoundException;
import com.example.blog_system_backend.common.UsernameAlreadyExistsException;
import com.example.blog_system_backend.user.dto.UserCreateRequest;
import com.example.blog_system_backend.user.dto.UserProfileResponse;
import com.example.blog_system_backend.user.dto.UserUpdateRequest;
import java.util.List;

import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
public class UserService {

    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;

    public UserService(UserRepository userRepository, PasswordEncoder passwordEncoder) {
        this.userRepository = userRepository;
        this.passwordEncoder = passwordEncoder;
    }

    public List<UserProfileResponse> getAll() {
        return userRepository.findAllByOrderByIdAsc().stream()
                .map(this::toResponse)
                .toList();
    }

    public UserProfileResponse getById(Long id) {
        return toResponse(findUserById(id));
    }

    public UserProfileResponse getByUsername(String username) {
        return toResponse(findUserByUsername(username));
    }

    @Transactional
    public UserProfileResponse create(UserCreateRequest request) {
        String username = normalizeUsername(request.username());
        validateUsernameLength(username);

        if (userRepository.existsByUsername(username)) {
            throw new UsernameAlreadyExistsException(username);
        }

        User user = new User();
        user.setUsername(username);
        user.setPassword(passwordEncoder.encode(request.password()));
        user.setRole(request.role() != null ? request.role() : Role.USER);

        return toResponse(userRepository.save(user));
    }

    @Transactional
    public UserProfileResponse update(Long id, UserUpdateRequest request) {
        User user = findUserById(id);
        String username = normalizeUsername(request.username());
        validateUsernameLength(username);

        if (userRepository.existsByUsernameAndIdNot(username, id)) {
            throw new UsernameAlreadyExistsException(username);
        }

        user.setUsername(username);

        if (request.password() != null) {
            if (request.password().isBlank()) {
                throw new IllegalArgumentException("password must not be blank");
            }
            user.setPassword(passwordEncoder.encode(request.password()));
        }

        if (request.role() != null) {
            user.setRole(request.role());
        }

        return toResponse(userRepository.save(user));
    }

    @Transactional
    public void delete(Long id) {
        if (!userRepository.existsById(id)) {
            throw new UserNotFoundException(id);
        }
        userRepository.deleteById(id);
    }

    private User findUserById(Long id) {
        return userRepository.findById(id)
                .orElseThrow(() -> new UserNotFoundException(id));
    }

    public User findUserByUsername(String username) {
        String normalizedUsername = normalizeUsername(username);
        return userRepository.findByUsername(normalizedUsername)
                .orElseThrow(() -> new UserNotFoundException(normalizedUsername));
    }

    private String normalizeUsername(String username) {
        if (username == null) {
            throw new IllegalArgumentException("username must not be blank");
        }
        return username.trim();
    }

    private void validateUsernameLength(String username) {
        if (username.isBlank()) {
            throw new IllegalArgumentException("username must not be blank");
        }
        if (username.length() < 3 || username.length() > 50) {
            throw new IllegalArgumentException("username length must be between 3 and 50");
        }
    }

    private UserProfileResponse toResponse(User user) {
        return new UserProfileResponse(
                user.getId(),
                user.getUsername(),
                user.getRole().name(),
                user.getCreatedAt()
        );
    }
}


