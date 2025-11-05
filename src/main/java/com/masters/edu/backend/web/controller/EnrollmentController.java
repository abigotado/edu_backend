package com.masters.edu.backend.web.controller;

import java.util.List;

import com.masters.edu.backend.domain.enrollment.Enrollment;
import com.masters.edu.backend.domain.enrollment.EnrollmentStatus;
import com.masters.edu.backend.service.enrollment.EnrollmentService;
import com.masters.edu.backend.web.dto.enrollment.EnrollRequest;
import com.masters.edu.backend.web.dto.enrollment.EnrollmentDto;
import com.masters.edu.backend.web.mapper.EnrollmentMapper;

import jakarta.validation.Valid;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api")
public class EnrollmentController {

    private final EnrollmentService enrollmentService;
    private final EnrollmentMapper enrollmentMapper;

    public EnrollmentController(EnrollmentService enrollmentService, EnrollmentMapper enrollmentMapper) {
        this.enrollmentService = enrollmentService;
        this.enrollmentMapper = enrollmentMapper;
    }

    @GetMapping("/courses/{courseId}/enrollments")
    @PreAuthorize("hasRole('TEACHER') or hasRole('ADMIN')")
    public ResponseEntity<List<EnrollmentDto>> listCourseEnrollments(@PathVariable Long courseId) {
        List<Enrollment> enrollments = enrollmentService.enrollmentsForCourse(courseId);
        return ResponseEntity.ok(enrollmentMapper.toDtos(enrollments));
    }

    @GetMapping("/students/{studentId}/enrollments")
    public ResponseEntity<List<EnrollmentDto>> listStudentEnrollments(
            @PathVariable Long studentId,
            @RequestParam(required = false) EnrollmentStatus status) {
        List<Enrollment> enrollments = status != null
                ? enrollmentService.enrollmentsForStudent(studentId, status)
                : enrollmentService.enrollmentsForStudent(studentId);
        return ResponseEntity.ok(enrollmentMapper.toDtos(enrollments));
    }

    @PostMapping("/courses/{courseId}/enrollments")
    @PreAuthorize("hasRole('STUDENT') or hasRole('ADMIN')")
    public ResponseEntity<EnrollmentDto> enroll(@PathVariable Long courseId,
            @Valid @RequestBody EnrollRequest request) {
        Enrollment enrollment = enrollmentService.enrollStudent(courseId, request.studentId());
        return ResponseEntity.status(HttpStatus.CREATED).body(enrollmentMapper.toDto(enrollment));
    }

    @DeleteMapping("/enrollments/{enrollmentId}")
    @PreAuthorize("hasRole('STUDENT') or hasRole('ADMIN')")
    public ResponseEntity<Void> unenroll(@PathVariable Long enrollmentId) {
        enrollmentService.unenroll(enrollmentId);
        return ResponseEntity.noContent().build();
    }
}


