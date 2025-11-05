package com.masters.edu.backend.repository.lesson;

import java.util.List;

import com.masters.edu.backend.domain.lesson.Lesson;

import org.springframework.data.jpa.repository.JpaRepository;

public interface LessonRepository extends JpaRepository<Lesson, Long> {

    List<Lesson> findByModuleIdOrderByOrderIndexAsc(Long moduleId);
}


