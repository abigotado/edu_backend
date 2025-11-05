package com.masters.edu.backend.domain.quiz;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import com.masters.edu.backend.domain.common.BaseEntity;
import com.masters.edu.backend.domain.course.Course;
import com.masters.edu.backend.domain.lesson.Module;

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
import jakarta.persistence.OneToOne;
import jakarta.persistence.Table;

import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@Entity
@NoArgsConstructor
@Table(name = "quizzes", indexes = {
        @Index(name = "idx_quiz_course", columnList = "course_id")
})
public class Quiz extends BaseEntity {

    @Column(name = "title", nullable = false, length = 255)
    private String title;

    @Column(name = "description", columnDefinition = "text")
    private String description;

    @Column(name = "total_points")
    private Integer totalPoints;

    @Column(name = "time_limit_seconds")
    private Integer timeLimitSeconds;

    @Column(name = "attempt_limit")
    private Integer attemptLimit;

    @Enumerated(EnumType.STRING)
    @Column(name = "grading_method", nullable = false, length = 20)
    private QuizGradingMethod gradingMethod = QuizGradingMethod.HIGHEST;

    @Enumerated(EnumType.STRING)
    @Column(name = "quiz_type", nullable = false, length = 20)
    private QuizType quizType = QuizType.MODULE_FINAL;

    @OneToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "module_id", unique = true)
    private Module module;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "course_id")
    private Course course;

    @OneToMany(mappedBy = "quiz", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<Question> questions = new ArrayList<>();

    @OneToMany(mappedBy = "quiz", cascade = CascadeType.ALL, orphanRemoval = true)
    private Set<QuizSubmission> submissions = new HashSet<>();
}


