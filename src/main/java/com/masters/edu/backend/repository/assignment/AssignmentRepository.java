package com.masters.edu.backend.repository.assignment;

import java.util.List;

import com.masters.edu.backend.domain.assignment.Assignment;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

public interface AssignmentRepository extends JpaRepository<Assignment, Long> {

    List<Assignment> findByLessonModuleCourseId(Long courseId);

    @Query("select a from Assignment a join fetch a.lesson l join fetch l.module m where a.id = :id")
    Assignment findDetailedById(Long id);
}


