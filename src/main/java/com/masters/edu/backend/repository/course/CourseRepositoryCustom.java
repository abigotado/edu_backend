package com.masters.edu.backend.repository.course;

import java.util.List;

import com.masters.edu.backend.domain.course.Course;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

public interface CourseRepositoryCustom {

    Page<Course> search(String text, Pageable pageable);

    List<Course> findByTeacherWithModules(Long teacherId);
}


