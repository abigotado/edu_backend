package com.masters.edu.backend.repository.course;

import com.masters.edu.backend.domain.course.Category;

import org.springframework.data.jpa.repository.JpaRepository;

public interface CategoryRepository extends JpaRepository<Category, Long> {
}


