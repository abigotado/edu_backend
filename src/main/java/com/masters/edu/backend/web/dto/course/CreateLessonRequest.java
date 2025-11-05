package com.masters.edu.backend.web.dto.course;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;

public record CreateLessonRequest(
        @NotBlank String title,
        String summary,
        String content,
        String videoUrl,
        Integer durationMinutes,
        @NotNull Integer orderIndex) {
}


