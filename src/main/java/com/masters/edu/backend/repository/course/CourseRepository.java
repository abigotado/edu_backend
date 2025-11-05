package com.masters.edu.backend.repository.course;

import java.util.List;
import java.util.Optional;

import com.masters.edu.backend.domain.course.Course;
import com.masters.edu.backend.domain.course.CourseStatus;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.EntityGraph;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

public interface CourseRepository extends JpaRepository<Course, Long>, CourseRepositoryCustom {

    Optional<Course> findBySlugAndTeacherId(String slug, Long teacherId);

    @EntityGraph(attributePaths = {"teacher", "category"})
    Page<Course> findByStatus(CourseStatus status, Pageable pageable);

    @Query("select distinct c from Course c " +
            "left join fetch c.teacher " +
            "left join fetch c.category " +
            "left join fetch c.modules m " +
            "left join fetch m.lessons " +
            "where c.id = :id")
    Optional<Course> findDetailedById(Long id);

    @EntityGraph(attributePaths = {"category", "teacher"})
    List<Course> findTop10ByStatusOrderByPublishedAtDesc(CourseStatus status);
}


