package com.masters.edu.backend.web.dto.assignment;

import jakarta.validation.constraints.NotBlank;

public record SubmissionRequest(
        @NotBlank String content,
        String attachmentPath) {
}


