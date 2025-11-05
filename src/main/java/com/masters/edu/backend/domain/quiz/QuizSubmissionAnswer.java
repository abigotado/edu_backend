package com.masters.edu.backend.domain.quiz;

import com.masters.edu.backend.domain.common.BaseEntity;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
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
@Table(name = "quiz_submission_answers", indexes = {
        @Index(name = "idx_submission_answer_submission", columnList = "quiz_submission_id"),
        @Index(name = "idx_submission_answer_question", columnList = "question_id")
}, uniqueConstraints = {
        @UniqueConstraint(name = "uk_submission_answer_unique", columnNames = {"quiz_submission_id", "question_id", "answer_option_id"})
})
public class QuizSubmissionAnswer extends BaseEntity {

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "quiz_submission_id", nullable = false)
    private QuizSubmission quizSubmission;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "question_id", nullable = false)
    private Question question;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "answer_option_id")
    private AnswerOption answerOption;

    @Column(name = "answer_text", columnDefinition = "text")
    private String answerText;

    @Column(name = "is_correct")
    private Boolean correct;

    @Column(name = "awarded_points")
    private Integer awardedPoints;
}


