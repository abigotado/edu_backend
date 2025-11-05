package com.masters.edu.backend.web.dto.assignment;

import java.time.OffsetDateTime;

import com.masters.edu.backend.domain.assignment.AssignmentStatus;
import com.masters.edu.backend.domain.assignment.GradingType;
import com.masters.edu.backend.domain.assignment.SubmissionType;

public record AssignmentDto(
        Long id,
        Long lessonId,
        String lessonTitle,
        String title,
        String description,
        OffsetDateTime dueAt,
        Integer maxScore,
        SubmissionType submissionType,
        GradingType gradingType,
        AssignmentStatus status,
        Integer orderIndex,
        OffsetDateTime releaseAt,
        boolean allowLateSubmission,
        Integer latePenaltyPercentage,
        boolean lockAfterDeadline,
        boolean discussionEnabled,
        OffsetDateTime gradingDeadline) {
}


