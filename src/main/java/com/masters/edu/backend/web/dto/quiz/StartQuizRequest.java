package com.masters.edu.backend.web.dto.quiz;

import jakarta.validation.constraints.NotNull;

public record StartQuizRequest(@NotNull Long studentId) {
}


