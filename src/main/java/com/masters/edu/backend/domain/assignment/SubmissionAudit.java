package com.masters.edu.backend.domain.assignment;

import java.time.OffsetDateTime;

import jakarta.persistence.Column;
import jakarta.persistence.Embeddable;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@Embeddable
@NoArgsConstructor
@AllArgsConstructor(staticName = "of")
public class SubmissionAudit {

    @Column(name = "graded_at")
    private OffsetDateTime gradedAt;

    @Column(name = "graded_by")
    private Long gradedById;

    @Column(name = "feedback_updated_at")
    private OffsetDateTime feedbackUpdatedAt;

    public static SubmissionAudit empty() {
        return new SubmissionAudit(null, null, null);
    }
}


