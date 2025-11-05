package com.masters.edu.backend.web.controller;

import java.util.List;

import com.masters.edu.backend.domain.assignment.Assignment;
import com.masters.edu.backend.domain.assignment.AssignmentSettings;
import com.masters.edu.backend.domain.assignment.Submission;
import com.masters.edu.backend.security.UserPrincipal;
import com.masters.edu.backend.service.assignment.AssignmentService;
import com.masters.edu.backend.web.dto.assignment.AssignmentDto;
import com.masters.edu.backend.web.dto.assignment.CreateAssignmentRequest;
import com.masters.edu.backend.web.dto.assignment.GradeSubmissionRequest;
import com.masters.edu.backend.web.dto.assignment.SubmissionDto;
import com.masters.edu.backend.web.dto.assignment.SubmissionRequest;
import com.masters.edu.backend.web.mapper.AssignmentMapper;

import jakarta.validation.Valid;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PatchMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api")
public class AssignmentController {

    private final AssignmentService assignmentService;
    private final AssignmentMapper assignmentMapper;

    public AssignmentController(AssignmentService assignmentService, AssignmentMapper assignmentMapper) {
        this.assignmentService = assignmentService;
        this.assignmentMapper = assignmentMapper;
    }

    @GetMapping("/courses/{courseId}/assignments")
    public ResponseEntity<List<AssignmentDto>> listAssignmentsForCourse(@PathVariable Long courseId) {
        List<Assignment> assignments = assignmentService.assignmentsForCourse(courseId);
        return ResponseEntity.ok(assignmentMapper.toDtos(assignments));
    }

    @GetMapping("/lessons/{lessonId}/assignments")
    public ResponseEntity<List<AssignmentDto>> listAssignmentsForLesson(@PathVariable Long lessonId) {
        List<Assignment> assignments = assignmentService.assignmentsForLesson(lessonId);
        return ResponseEntity.ok(assignmentMapper.toDtos(assignments));
    }

    @GetMapping("/assignments/{assignmentId}")
    public ResponseEntity<AssignmentDto> getAssignment(@PathVariable Long assignmentId) {
        Assignment assignment = assignmentService.getAssignment(assignmentId);
        return ResponseEntity.ok(assignmentMapper.toDto(assignment));
    }

    @PostMapping("/lessons/{lessonId}/assignments")
    @PreAuthorize("hasRole('TEACHER') or hasRole('ADMIN')")
    public ResponseEntity<AssignmentDto> createAssignment(@PathVariable Long lessonId,
            @Valid @RequestBody CreateAssignmentRequest request,
            @AuthenticationPrincipal UserPrincipal principal) {
        Assignment assignment = mapToAssignmentEntity(request);
        Assignment created = assignmentService.createAssignment(lessonId, assignment, principal != null ? principal.getId() : null);
        return ResponseEntity.status(HttpStatus.CREATED).body(assignmentMapper.toDto(created));
    }

    @PatchMapping("/assignments/{assignmentId}/publish")
    @PreAuthorize("hasRole('TEACHER') or hasRole('ADMIN')")
    public ResponseEntity<AssignmentDto> publishAssignment(@PathVariable Long assignmentId) {
        Assignment assignment = assignmentService.publish(assignmentId);
        return ResponseEntity.ok(assignmentMapper.toDto(assignment));
    }

    @PostMapping("/assignments/{assignmentId}/submissions")
    @PreAuthorize("hasRole('STUDENT') or hasRole('ADMIN')")
    public ResponseEntity<SubmissionDto> submitAssignment(@PathVariable Long assignmentId,
            @Valid @RequestBody SubmissionRequest request,
            @AuthenticationPrincipal UserPrincipal principal) {
        Long studentId = requirePrincipalId(principal);
        Submission submissionEntity = new Submission();
        submissionEntity.setContent(request.content());
        submissionEntity.setAttachmentPath(request.attachmentPath());
        Submission submission = assignmentService.submit(assignmentId, studentId, submissionEntity);
        return ResponseEntity.status(HttpStatus.CREATED).body(assignmentMapper.toSubmissionDto(submission));
    }

    @GetMapping("/assignments/{assignmentId}/submissions")
    @PreAuthorize("hasRole('TEACHER') or hasRole('ADMIN')")
    public ResponseEntity<List<SubmissionDto>> listSubmissionsForAssignment(@PathVariable Long assignmentId) {
        List<Submission> submissions = assignmentService.submissionsForAssignment(assignmentId);
        return ResponseEntity.ok(assignmentMapper.toSubmissionDtos(submissions));
    }

    @GetMapping("/students/{studentId}/submissions")
    @PreAuthorize("hasRole('STUDENT') or hasRole('ADMIN')")
    public ResponseEntity<List<SubmissionDto>> listSubmissionsForStudent(@PathVariable Long studentId,
            @AuthenticationPrincipal UserPrincipal principal) {
        ensureAccessToStudentData(studentId, principal);
        List<Submission> submissions = assignmentService.submissionsForStudent(studentId);
        return ResponseEntity.ok(assignmentMapper.toSubmissionDtos(submissions));
    }

    @PatchMapping("/submissions/{submissionId}/grade")
    @PreAuthorize("hasRole('TEACHER') or hasRole('ADMIN')")
    public ResponseEntity<SubmissionDto> gradeSubmission(@PathVariable Long submissionId,
            @Valid @RequestBody GradeSubmissionRequest request,
            @AuthenticationPrincipal UserPrincipal principal) {
        Long graderId = requirePrincipalId(principal);
        Submission submission = assignmentService.grade(submissionId, graderId, request.score(), request.feedback());
        return ResponseEntity.ok(assignmentMapper.toSubmissionDto(submission));
    }

    private Assignment mapToAssignmentEntity(CreateAssignmentRequest request) {
        Assignment assignment = new Assignment();
        assignment.setTitle(request.title());
        assignment.setDescription(request.description());
        assignment.setDueAt(request.dueAt());
        assignment.setMaxScore(request.maxScore());
        assignment.setSubmissionType(request.submissionType());
        assignment.setGradingType(request.gradingType());
        assignment.setOrderIndex(request.orderIndex());
        assignment.setReleaseAt(request.releaseAt());

        boolean allowLate = Boolean.TRUE.equals(request.allowLateSubmission());
        boolean lockAfterDeadline = Boolean.TRUE.equals(request.lockAfterDeadline());
        boolean discussionEnabled = request.discussionEnabled() == null ? true : request.discussionEnabled();
        AssignmentSettings settings = AssignmentSettings.of(
                allowLate,
                request.latePenaltyPercentage(),
                lockAfterDeadline,
                discussionEnabled,
                request.gradingDeadline());
        assignment.setSettings(settings);
        return assignment;
    }

    private Long requirePrincipalId(UserPrincipal principal) {
        if (principal == null) {
            throw new AccessDeniedException("Authentication required");
        }
        return principal.getId();
    }

    private void ensureAccessToStudentData(Long studentId, UserPrincipal principal) {
        if (principal == null) {
            throw new AccessDeniedException("Authentication required");
        }
        if (principal.getRole().equals("STUDENT") && !principal.getId().equals(studentId)) {
            throw new AccessDeniedException("Cannot access other student's submissions");
        }
    }
}


