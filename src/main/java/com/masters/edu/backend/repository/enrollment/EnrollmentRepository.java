package com.masters.edu.backend.repository.enrollment;

import java.util.List;
import java.util.Optional;

import com.masters.edu.backend.domain.enrollment.Enrollment;
import com.masters.edu.backend.domain.enrollment.EnrollmentStatus;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;

public interface EnrollmentRepository extends JpaRepository<Enrollment, Long> {

    Optional<Enrollment> findByCourseIdAndStudentId(Long courseId, Long studentId);

    List<Enrollment> findByStudentIdAndStatus(Long studentId, EnrollmentStatus status);

    List<Enrollment> findByCourseId(Long courseId);

    List<Enrollment> findByStudentId(Long studentId);

    @Query("select count(e) from Enrollment e where e.course.id = :courseId and e.status = :status")
    long countByCourseAndStatus(Long courseId, EnrollmentStatus status);

    @Modifying(clearAutomatically = true)
    @Query("update Enrollment e set e.status = :status where e.id = :id")
    int updateStatus(Long id, EnrollmentStatus status);
}


