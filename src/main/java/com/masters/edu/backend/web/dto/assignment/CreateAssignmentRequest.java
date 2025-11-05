package com.masters.edu.backend.web.dto.assignment;

import java.time.OffsetDateTime;

import com.masters.edu.backend.domain.assignment.GradingType;
import com.masters.edu.backend.domain.assignment.SubmissionType;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;

public record CreateAssignmentRequest(
        @NotBlank String title,
        String description,
        OffsetDateTime dueAt,
        Integer maxScore,
        @NotNull SubmissionType submissionType,
        @NotNull GradingType gradingType,
        @NotNull Integer orderIndex,
        OffsetDateTime releaseAt,
        Boolean allowLateSubmission,
        Integer latePenaltyPercentage,
        Boolean lockAfterDeadline,
        Boolean discussionEnabled,
        OffsetDateTime gradingDeadline) {
}


