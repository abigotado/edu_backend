package com.masters.edu.backend.web.dto.quiz;

import java.util.List;

public record SubmitQuizRequest(
        Integer score,
        List<QuizAnswerRequest> answers) {
}


