package com.masters.edu.backend.web.dto.quiz;

public record AnswerOptionDto(
        Long id,
        String text,
        boolean correct,
        String feedback) {
}


