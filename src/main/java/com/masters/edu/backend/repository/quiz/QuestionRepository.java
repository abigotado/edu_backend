package com.masters.edu.backend.repository.quiz;

import com.masters.edu.backend.domain.quiz.Question;

import org.springframework.data.jpa.repository.JpaRepository;

public interface QuestionRepository extends JpaRepository<Question, Long> {
}


