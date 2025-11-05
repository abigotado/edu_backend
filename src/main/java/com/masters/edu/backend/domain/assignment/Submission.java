package com.masters.edu.backend.domain.assignment;

import java.time.OffsetDateTime;

import com.masters.edu.backend.domain.common.BaseEntity;
import com.masters.edu.backend.domain.user.User;

import jakarta.persistence.Column;
import jakarta.persistence.Embedded;
import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.FetchType;
import jakarta.persistence.Index;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.Table;
import jakarta.persistence.UniqueConstraint;

import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@Entity
@NoArgsConstructor
@Table(name = "submissions", indexes = {
        @Index(name = "idx_submission_assignment", columnList = "assignment_id"),
        @Index(name = "idx_submission_student", columnList = "student_id")
}, uniqueConstraints = {
        @UniqueConstraint(name = "uk_submission_assignment_student", columnNames = {"assignment_id", "student_id"})
})
public class Submission extends BaseEntity {

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "assignment_id", nullable = false)
    private Assignment assignment;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "student_id", nullable = false)
    private User student;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "grader_id")
    private User grader;

    @Enumerated(EnumType.STRING)
    @Column(name = "status", nullable = false, length = 25)
    private SubmissionStatus status = SubmissionStatus.DRAFT;

    @Column(name = "submitted_at")
    private OffsetDateTime submittedAt;

    @Column(name = "content", columnDefinition = "text")
    private String content;

    @Column(name = "attachment_path", length = 512)
    private String attachmentPath;

    @Column(name = "score")
    private Integer score;

    @Column(name = "feedback", columnDefinition = "text")
    private String feedback;

    @Embedded
    private SubmissionAudit audit = SubmissionAudit.empty();
}


