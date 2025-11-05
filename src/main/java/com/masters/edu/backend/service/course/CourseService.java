package com.masters.edu.backend.service.course;

import java.util.Comparator;
import java.util.List;

import com.masters.edu.backend.domain.course.Category;
import com.masters.edu.backend.domain.course.Course;
import com.masters.edu.backend.domain.course.CourseStatus;
import com.masters.edu.backend.domain.lesson.Module;
import com.masters.edu.backend.domain.user.User;
import com.masters.edu.backend.repository.course.CategoryRepository;
import com.masters.edu.backend.repository.course.CourseRepository;
import com.masters.edu.backend.repository.user.UserRepository;
import com.masters.edu.backend.web.dto.course.CreateCourseRequest;

import jakarta.persistence.EntityNotFoundException;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@Transactional(readOnly = true)
public class CourseService {

    private final CourseRepository courseRepository;
    private final CategoryRepository categoryRepository;
    private final UserRepository userRepository;

    public CourseService(CourseRepository courseRepository,
            CategoryRepository categoryRepository,
            UserRepository userRepository) {
        this.courseRepository = courseRepository;
        this.categoryRepository = categoryRepository;
        this.userRepository = userRepository;
    }

    public Course getCourse(Long id) {
        Course course = courseRepository.findDetailedById(id)
                .orElseThrow(() -> new EntityNotFoundException("Course not found: " + id));
        course.getModules().sort(Comparator.comparing(Module::getOrderIndex));
        return course;
    }

    @Transactional
    public Course createCourse(CreateCourseRequest request) {
        Course course = new Course();
        course.setTitle(request.title());
        course.setSlug(request.slug());
        course.setSummary(request.summary());
        course.setDescription(request.description());
        course.setDifficultyLevel(request.difficultyLevel());
        course.setEstimatedDuration(request.estimatedDuration());
        course.setLanguage(request.language());
        course.setStatus(CourseStatus.DRAFT);

        if (request.categoryId() != null) {
            Category category = categoryRepository.findById(request.categoryId())
                    .orElseThrow(() -> new EntityNotFoundException("Category not found: " + request.categoryId()));
            course.setCategory(category);
        }

        User teacher = userRepository.findById(request.teacherId())
                .orElseThrow(() -> new EntityNotFoundException("Teacher not found: " + request.teacherId()));
        course.setTeacher(teacher);

        return courseRepository.save(course);
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


