package com.masters.edu.backend.web.dto.assignment;

import java.time.OffsetDateTime;

import com.masters.edu.backend.domain.assignment.SubmissionStatus;

public record SubmissionDto(
        Long id,
        Long assignmentId,
        String assignmentTitle,
        Long studentId,
        String studentName,
        SubmissionStatus status,
        OffsetDateTime submittedAt,
        String content,
        String attachmentPath,
        Integer score,
        String feedback,
        Long graderId,
        String graderName,
        OffsetDateTime gradedAt) {
}


