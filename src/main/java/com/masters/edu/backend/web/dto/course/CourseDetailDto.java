package com.masters.edu.backend.web.dto.course;

import java.time.OffsetDateTime;
import java.util.List;

import com.masters.edu.backend.domain.course.CourseLevel;
import com.masters.edu.backend.domain.course.CourseStatus;

public record CourseDetailDto(
        Long id,
        String title,
        String slug,
        String summary,
        String description,
        CourseLevel difficultyLevel,
        CourseStatus status,
        String teacherName,
        String categoryName,
        String estimatedDuration,
        String language,
        OffsetDateTime publishedAt,
        List<ModuleDto> modules) {
}


