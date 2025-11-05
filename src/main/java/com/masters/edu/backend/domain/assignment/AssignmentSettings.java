package com.masters.edu.backend.domain.assignment;

import java.time.OffsetDateTime;

import jakarta.persistence.Column;
import jakarta.persistence.Embeddable;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@Embeddable
@NoArgsConstructor
@AllArgsConstructor(staticName = "of")
public class AssignmentSettings {

    @Column(name = "allow_late_submission", nullable = false)
    private boolean allowLateSubmission;

    @Column(name = "late_penalty_percentage")
    private Integer latePenaltyPercentage;

    @Column(name = "lock_after_deadline", nullable = false)
    private boolean lockAfterDeadline;

    @Column(name = "discussion_enabled", nullable = false)
    private boolean discussionEnabled;

    @Column(name = "grading_deadline")
    private OffsetDateTime gradingDeadline;

    public static AssignmentSettings defaults() {
        return new AssignmentSettings(false, null, false, true, null);
    }
}


