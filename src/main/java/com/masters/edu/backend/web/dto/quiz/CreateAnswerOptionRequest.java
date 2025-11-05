package com.masters.edu.backend.web.dto.quiz;

import jakarta.validation.constraints.NotBlank;

public record CreateAnswerOptionRequest(
        @NotBlank String text,
        boolean correct,
        String feedback,
        Integer orderIndex) {
}


