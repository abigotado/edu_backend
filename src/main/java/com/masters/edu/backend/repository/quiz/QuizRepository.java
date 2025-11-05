package com.masters.edu.backend.repository.quiz;

import java.util.List;

import com.masters.edu.backend.domain.quiz.Quiz;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

public interface QuizRepository extends JpaRepository<Quiz, Long> {

    List<Quiz> findByCourseId(Long courseId);

    @Query("select q from Quiz q join fetch q.questions qs left join fetch qs.options where q.id = :id")
    Quiz findDetailedById(Long id);
}


