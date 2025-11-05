package com.masters.edu.backend.web.dto.course;

import com.masters.edu.backend.domain.course.CourseLevel;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;

public record CreateCourseRequest(
        @NotBlank String title,
        @NotBlank String slug,
        @Size(max = 500) String summary,
        String description,
        @NotNull CourseLevel difficultyLevel,
        String estimatedDuration,
        String language,
        Long categoryId,
        @NotNull Long teacherId) {
}


