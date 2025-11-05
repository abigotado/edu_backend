package com.masters.edu.backend.repository.lesson;

import java.util.List;

import com.masters.edu.backend.domain.lesson.Module;

import org.springframework.data.jpa.repository.EntityGraph;
import org.springframework.data.jpa.repository.JpaRepository;

public interface ModuleRepository extends JpaRepository<Module, Long> {

    @EntityGraph(attributePaths = {"lessons"})
    List<Module> findByCourseIdOrderByOrderIndexAsc(Long courseId);
}


