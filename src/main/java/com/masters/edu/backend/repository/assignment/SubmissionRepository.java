package com.masters.edu.backend.repository.assignment;

import java.util.List;
import java.util.Optional;

import com.masters.edu.backend.domain.assignment.Submission;
import com.masters.edu.backend.domain.assignment.SubmissionStatus;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

public interface SubmissionRepository extends JpaRepository<Submission, Long> {

    Optional<Submission> findByAssignmentIdAndStudentId(Long assignmentId, Long studentId);

    List<Submission> findByAssignmentId(Long assignmentId);

    List<Submission> findByStudentId(Long studentId);

    long countByAssignmentIdAndStatus(Long assignmentId, SubmissionStatus status);

    @Query("select s from Submission s join fetch s.assignment where s.id = :id")
    Optional<Submission> findDetailedById(Long id);
}


