package com.masters.edu.backend.domain.assignment;

import java.time.OffsetDateTime;
import java.util.ArrayList;
import java.util.List;

import com.masters.edu.backend.domain.common.BaseEntity;
import com.masters.edu.backend.domain.lesson.Lesson;
import com.masters.edu.backend.domain.user.User;

import jakarta.persistence.CascadeType;
import jakarta.persistence.Column;
import jakarta.persistence.Embedded;
import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.FetchType;
import jakarta.persistence.Index;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.OneToMany;
import jakarta.persistence.Table;

import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@Entity
@NoArgsConstructor
@Table(name = "assignments", indexes = {
        @Index(name = "idx_assignment_lesson", columnList = "lesson_id")
})
public class Assignment extends BaseEntity {

    @Column(name = "title", nullable = false, length = 255)
    private String title;

    @Column(name = "description", columnDefinition = "text")
    private String description;

    @Column(name = "due_at")
    private OffsetDateTime dueAt;

    @Column(name = "max_score")
    private Integer maxScore;

    @Enumerated(EnumType.STRING)
    @Column(name = "submission_type", nullable = false, length = 20)
    private SubmissionType submissionType = SubmissionType.TEXT;

    @Enumerated(EnumType.STRING)
    @Column(name = "grading_type", nullable = false, length = 20)
    private GradingType gradingType = GradingType.NUMERIC;

    @Enumerated(EnumType.STRING)
    @Column(name = "status", nullable = false, length = 20)
    private AssignmentStatus status = AssignmentStatus.DRAFT;

    @Column(name = "order_index", nullable = false)
    private Integer orderIndex;

    @Column(name = "release_at")
    private OffsetDateTime releaseAt;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "lesson_id", nullable = false)
    private Lesson lesson;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "creator_id")
    private User creator;

    @OneToMany(mappedBy = "assignment", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<Submission> submissions = new ArrayList<>();

    @Embedded
    private AssignmentSettings settings = AssignmentSettings.defaults();
}


