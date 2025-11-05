package com.masters.edu.backend.repository.user;

import java.util.Optional;

import com.masters.edu.backend.domain.user.User;
import com.masters.edu.backend.domain.user.UserRole;

import org.springframework.data.jpa.repository.EntityGraph;
import org.springframework.data.jpa.repository.JpaRepository;

public interface UserRepository extends JpaRepository<User, Long> {

    Optional<User> findByEmail(String email);

    boolean existsByEmail(String email);

    long countByRole(UserRole role);

    @EntityGraph(attributePaths = {"profile"})
    Optional<User> findWithProfileById(Long id);
}


