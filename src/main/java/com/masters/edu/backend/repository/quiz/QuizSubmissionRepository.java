package com.masters.edu.backend.repository.quiz;

import java.util.List;
import java.util.Optional;

import com.masters.edu.backend.domain.quiz.QuizSubmission;
import com.masters.edu.backend.domain.quiz.QuizSubmissionStatus;

import org.springframework.data.jpa.repository.EntityGraph;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

public interface QuizSubmissionRepository extends JpaRepository<QuizSubmission, Long> {

    List<QuizSubmission> findByQuizIdAndStudentId(Long quizId, Long studentId);

    long countByQuizIdAndStatus(Long quizId, QuizSubmissionStatus status);

    @EntityGraph(attributePaths = {"answers", "answers.answerOption"})
    Optional<QuizSubmission> findWithAnswersById(Long id);

    @Query("select qs from QuizSubmission qs join fetch qs.quiz where qs.id = :id")
    Optional<QuizSubmission> findDetailedById(Long id);
}


