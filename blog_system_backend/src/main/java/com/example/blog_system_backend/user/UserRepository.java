package com.example.blog_system_backend.user;

import java.util.List;
import java.util.Optional;
import org.springframework.data.jpa.repository.JpaRepository;

public interface UserRepository extends JpaRepository<User, Long> {

    List<User> findAllByOrderByIdAsc();

    Optional<User> findByUsername(String username);

    boolean existsByUsername(String username);

    boolean existsByUsernameAndIdNot(String username, Long id);
}
