package com.masters.edu.backend.web.dto.course;

public record LessonDto(Long id, String title, String summary, Integer orderIndex, Integer durationMinutes, String videoUrl) {
}


