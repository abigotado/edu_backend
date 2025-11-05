package com.masters.edu.backend.web.dto.quiz;

import jakarta.validation.constraints.NotNull;

public record QuizAnswerRequest(
        @NotNull Long questionId,
        Long answerOptionId,
        String answerText) {
}


