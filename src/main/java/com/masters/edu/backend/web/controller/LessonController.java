package com.masters.edu.backend.web.controller;

import java.util.List;

import com.masters.edu.backend.domain.lesson.Lesson;
import com.masters.edu.backend.service.course.LessonService;
import com.masters.edu.backend.web.dto.course.CreateLessonRequest;
import com.masters.edu.backend.web.dto.course.LessonDto;
import com.masters.edu.backend.web.mapper.CourseMapper;

import jakarta.validation.Valid;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/modules/{moduleId}/lessons")
public class LessonController {

    private final LessonService lessonService;
    private final CourseMapper courseMapper;

    public LessonController(LessonService lessonService, CourseMapper courseMapper) {
        this.lessonService = lessonService;
        this.courseMapper = courseMapper;
    }

    @GetMapping
    public ResponseEntity<List<LessonDto>> listLessons(@PathVariable Long moduleId) {
        List<Lesson> lessons = lessonService.lessonsForModule(moduleId);
        return ResponseEntity.ok(courseMapper.toLessonDtos(lessons));
    }

    @PostMapping
    @PreAuthorize("hasRole('TEACHER') or hasRole('ADMIN')")
    public ResponseEntity<LessonDto> createLesson(@PathVariable Long moduleId,
            @Valid @RequestBody CreateLessonRequest request) {
        Lesson lesson = lessonService.createLesson(moduleId, request);
        return ResponseEntity.status(HttpStatus.CREATED).body(courseMapper.toLessonDto(lesson));
    }
}


