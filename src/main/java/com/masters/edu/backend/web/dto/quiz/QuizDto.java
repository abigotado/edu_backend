package com.masters.edu.backend.web.dto.quiz;

import java.util.List;

import com.masters.edu.backend.domain.quiz.QuizGradingMethod;
import com.masters.edu.backend.domain.quiz.QuizType;

public record QuizDto(
        Long id,
        Long courseId,
        String courseTitle,
        Long moduleId,
        String moduleTitle,
        String title,
        String description,
        Integer totalPoints,
        Integer timeLimitSeconds,
        Integer attemptLimit,
        QuizGradingMethod gradingMethod,
        QuizType quizType,
        List<QuestionDto> questions) {
}


