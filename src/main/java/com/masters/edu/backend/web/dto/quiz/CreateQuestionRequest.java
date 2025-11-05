package com.masters.edu.backend.web.dto.quiz;

import java.util.List;

import com.masters.edu.backend.domain.quiz.QuestionType;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;

public record CreateQuestionRequest(
        @NotBlank String text,
        String explanation,
        @NotNull Integer points,
        @NotNull QuestionType questionType,
        @NotNull Integer orderIndex,
        List<CreateAnswerOptionRequest> options) {
}


