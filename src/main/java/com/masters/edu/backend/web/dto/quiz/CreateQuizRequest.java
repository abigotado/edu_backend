package com.masters.edu.backend.web.dto.quiz;

import java.util.List;

import com.masters.edu.backend.domain.quiz.QuizGradingMethod;
import com.masters.edu.backend.domain.quiz.QuizType;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;

public record CreateQuizRequest(
        @NotBlank String title,
        String description,
        Integer totalPoints,
        Integer timeLimitSeconds,
        Integer attemptLimit,
        @NotNull QuizGradingMethod gradingMethod,
        @NotNull QuizType quizType,
        List<CreateQuestionRequest> questions) {
}


