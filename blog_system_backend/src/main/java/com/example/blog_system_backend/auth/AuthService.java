package com.example.blog_system_backend.auth;

import com.example.blog_system_backend.auth.dto.AuthResponse;
import com.example.blog_system_backend.auth.dto.LoginRequest;
import com.example.blog_system_backend.auth.dto.RegisterRequest;
import com.example.blog_system_backend.security.JwtService;
import com.example.blog_system_backend.user.Role;
import com.example.blog_system_backend.user.User;
import com.example.blog_system_backend.user.UserRepository;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
public class AuthService {

    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;
    private final AuthenticationManager authenticationManager;
    private final JwtService jwtService;

    public AuthService(
            UserRepository userRepository,
            PasswordEncoder passwordEncoder,
            AuthenticationManager authenticationManager,
            JwtService jwtService
    ) {
        this.userRepository = userRepository;
        this.passwordEncoder = passwordEncoder;
        this.authenticationManager = authenticationManager;
        this.jwtService = jwtService;
    }

    @Transactional
    public AuthResponse register(RegisterRequest request) {
        String username = request.username().trim();
        if (userRepository.existsByUsername(username)) {
            throw new IllegalArgumentException("username already exists");
        }

        User user = new User();
        user.setUsername(username);
        user.setPassword(passwordEncoder.encode(request.password()));
        user.setRole(Role.USER);

        User savedUser = userRepository.save(user);
        String token = jwtService.generateToken(savedUser);

        return new AuthResponse(token, "Bearer", savedUser.getUsername(), savedUser.getRole().name());
    }

    public AuthResponse login(LoginRequest request) {
        String username = request.username().trim();

        authenticationManager.authenticate(
                new UsernamePasswordAuthenticationToken(username, request.password())
        );

        User user = userRepository.findByUsername(username)
                .orElseThrow(() -> new IllegalArgumentException("user not found"));

        String token = jwtService.generateToken(user);
        return new AuthResponse(token, "Bearer", user.getUsername(), user.getRole().name());
    }
}
