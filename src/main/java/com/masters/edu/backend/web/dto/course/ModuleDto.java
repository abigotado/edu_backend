package com.masters.edu.backend.web.dto.course;

import java.util.List;

public record ModuleDto(Long id, String title, String description, Integer orderIndex, List<LessonDto> lessons) {
}



