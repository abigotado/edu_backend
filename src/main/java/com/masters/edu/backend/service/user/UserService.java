package com.masters.edu.backend.service.user;

import java.util.List;

import com.masters.edu.backend.domain.user.Profile;
import com.masters.edu.backend.domain.user.User;
import com.masters.edu.backend.domain.user.UserRole;
import com.masters.edu.backend.repository.user.UserRepository;

import jakarta.persistence.EntityNotFoundException;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@Transactional
public class UserService {

    private final UserRepository userRepository;

    public UserService(UserRepository userRepository) {
        this.userRepository = userRepository;
    }

    public User register(User user, Profile profile) {
        if (userRepository.existsByEmail(user.getEmail())) {
            throw new IllegalStateException("Email already in use");
        }
        user.setProfile(profile);
        profile.setUser(user);
        return userRepository.save(user);
    }

    public User getUser(Long id) {
        return userRepository.findById(id)
                .orElseThrow(() -> new EntityNotFoundException("User not found: " + id));
    }

    public User getUserWithProfile(Long id) {
        return userRepository.findWithProfileById(id)
                .orElseThrow(() -> new EntityNotFoundException("User not found: " + id));
    }

    public List<User> listTeachers() {
        return userRepository.findAll().stream()
                .filter(u -> u.getRole() == UserRole.TEACHER)
                .toList();
    }
}


