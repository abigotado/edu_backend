package com.masters.edu.backend.web.dto.quiz;

import java.time.OffsetDateTime;

import com.masters.edu.backend.domain.quiz.QuizSubmissionStatus;

public record QuizSubmissionDto(
        Long id,
        Long quizId,
        String quizTitle,
        Long studentId,
        String studentName,
        QuizSubmissionStatus status,
        Integer score,
        OffsetDateTime startedAt,
        OffsetDateTime completedAt,
        Integer attemptNumber) {
}


