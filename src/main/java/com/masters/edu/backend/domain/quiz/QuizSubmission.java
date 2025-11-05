package com.masters.edu.backend.domain.quiz;

import java.time.OffsetDateTime;
import java.util.HashSet;
import java.util.Set;

import com.masters.edu.backend.domain.common.BaseEntity;
import com.masters.edu.backend.domain.enrollment.Enrollment;
import com.masters.edu.backend.domain.user.User;

import jakarta.persistence.CascadeType;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.FetchType;
import jakarta.persistence.Index;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.OneToMany;
import jakarta.persistence.Table;
import jakarta.persistence.UniqueConstraint;

import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@Entity
@NoArgsConstructor
@Table(name = "quiz_submissions", indexes = {
        @Index(name = "idx_quiz_submission_quiz", columnList = "quiz_id"),
        @Index(name = "idx_quiz_submission_student", columnList = "student_id")
}, uniqueConstraints = {
        @UniqueConstraint(name = "uk_quiz_submission_attempt", columnNames = {"quiz_id", "student_id", "attempt_number"})
})
public class QuizSubmission extends BaseEntity {

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "quiz_id", nullable = false)
    private Quiz quiz;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "student_id", nullable = false)
    private User student;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "enrollment_id")
    private Enrollment enrollment;

    @Enumerated(EnumType.STRING)
    @Column(name = "status", nullable = false, length = 20)
    private QuizSubmissionStatus status = QuizSubmissionStatus.IN_PROGRESS;

    @Column(name = "started_at")
    private OffsetDateTime startedAt;

    @Column(name = "completed_at")
    private OffsetDateTime completedAt;

    @Column(name = "score")
    private Integer score;

    @Column(name = "attempt_number", nullable = false)
    private Integer attemptNumber = 1;

    @OneToMany(mappedBy = "quizSubmission", cascade = CascadeType.ALL, orphanRemoval = true)
    private Set<QuizSubmissionAnswer> answers = new HashSet<>();
}


