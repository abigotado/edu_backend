package com.masters.edu.backend.web.dto.quiz;

import java.util.List;

import com.masters.edu.backend.domain.quiz.QuestionType;

public record QuestionDto(
        Long id,
        String text,
        String explanation,
        Integer points,
        QuestionType questionType,
        Integer orderIndex,
        List<AnswerOptionDto> options) {
}


