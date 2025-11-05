package com.masters.edu.backend.web.dto.assignment;

import jakarta.validation.constraints.Max;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotNull;

public record GradeSubmissionRequest(
        @NotNull @Min(0) @Max(1000) Integer score,
        String feedback) {
}


