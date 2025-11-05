package com.masters.edu.backend.repository.course;

import java.util.List;
import java.util.Optional;

import com.masters.edu.backend.domain.course.CourseReview;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

public interface CourseReviewRepository extends JpaRepository<CourseReview, Long> {

    List<CourseReview> findByCourseId(Long courseId);

    List<CourseReview> findByStudentId(Long studentId);

    Optional<CourseReview> findByCourseIdAndStudentId(Long courseId, Long studentId);

    @Query("select avg(r.rating) from CourseReview r where r.course.id = :courseId")
    Double findAverageRatingByCourseId(Long courseId);
}


