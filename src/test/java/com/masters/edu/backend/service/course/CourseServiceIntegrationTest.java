package com.masters.edu.backend.service.course;

import static org.assertj.core.api.Assertions.assertThat;

import java.util.UUID;

import com.masters.edu.backend.IntegrationTestSupport;
import com.masters.edu.backend.domain.course.Course;
import com.masters.edu.backend.domain.course.CourseLevel;
import com.masters.edu.backend.domain.course.CourseStatus;
import com.masters.edu.backend.web.dto.course.CreateCourseRequest;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;

class CourseServiceIntegrationTest extends IntegrationTestSupport {

    @Autowired
    private CourseService courseService;

    @Test
    void createCourse_persistsWithDraftStatus() {
        CreateCourseRequest request = new CreateCourseRequest(
                "Интеграционный курс",
                "integration-course-" + UUID.randomUUID(),
                "Краткое описание",
                "Подробное описание",
                CourseLevel.BEGINNER,
                "4 недели",
                "RU",
                1L,
                2L);

        Course course = courseService.createCourse(request);

        assertThat(course.getId()).isNotNull();
        assertThat(course.getStatus()).isEqualTo(CourseStatus.DRAFT);
        assertThat(course.getTeacher().getId()).isEqualTo(2L);
        assertThat(course.getCategory()).isNotNull();
    }

    @Test
    void listCourses_returnsPublishedCourses() {
        var result = courseService.listCourses(CourseStatus.PUBLISHED, org.springframework.data.domain.PageRequest.of(0, 10));
        assertThat(result.getContent()).isNotEmpty();
        assertThat(result.getContent()).allMatch(course -> course.getStatus() == CourseStatus.PUBLISHED);
    }

    @Test
    void recentPublishedCourses_returnsLimitedList() {
        var courses = courseService.recentPublishedCourses(5);
        assertThat(courses).isNotEmpty();
        assertThat(courses).hasSizeLessThanOrEqualTo(5);
    }
}


