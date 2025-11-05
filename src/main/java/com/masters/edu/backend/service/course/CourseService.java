package com.masters.edu.backend.service.course;

import java.util.List;

import com.masters.edu.backend.domain.course.Course;
import com.masters.edu.backend.domain.course.CourseStatus;
import com.masters.edu.backend.repository.course.CourseRepository;

import jakarta.persistence.EntityNotFoundException;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@Transactional(readOnly = true)
public class CourseService {

    private final CourseRepository courseRepository;

    public CourseService(CourseRepository courseRepository) {
        this.courseRepository = courseRepository;
    }

    public Course getCourse(Long id) {
        return courseRepository.findDetailedById(id)
                .orElseThrow(() -> new EntityNotFoundException("Course not found: " + id));
    }

    public Page<Course> listCourses(CourseStatus status, Pageable pageable) {
        return courseRepository.findByStatus(status, pageable);
    }

    public List<Course> recentPublishedCourses(int max) {
        return courseRepository.findTop10ByStatusOrderByPublishedAtDesc(CourseStatus.PUBLISHED).stream()
                .limit(max)
                .toList();
    }

    @Transactional
    public Course publishCourse(Long id) {
        Course course = getCourse(id);
        course.setStatus(CourseStatus.PUBLISHED);
        courseRepository.save(course);
        return course;
    }

    @Transactional
    public Course archiveCourse(Long id) {
        Course course = getCourse(id);
        course.setStatus(CourseStatus.ARCHIVED);
        return courseRepository.save(course);
    }

    public Page<Course> search(String text, Pageable pageable) {
        return courseRepository.search(text, pageable);
    }

    public List<Course> coursesForTeacher(Long teacherId) {
        return courseRepository.findByTeacherWithModules(teacherId);
    }
}


