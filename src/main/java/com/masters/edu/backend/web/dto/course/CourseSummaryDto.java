package com.masters.edu.backend.web.dto.course;

import java.time.OffsetDateTime;

import com.masters.edu.backend.domain.course.CourseLevel;
import com.masters.edu.backend.domain.course.CourseStatus;

public record CourseSummaryDto(
        Long id,
        String title,
        String slug,
        String summary,
        CourseLevel difficultyLevel,
        CourseStatus status,
        String teacherName,
        String categoryName,
        String estimatedDuration,
        String language,
        OffsetDateTime publishedAt) {
}


