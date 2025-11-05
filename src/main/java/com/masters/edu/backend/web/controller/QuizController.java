package com.masters.edu.backend.web.controller;

import java.util.List;

import com.masters.edu.backend.domain.quiz.Quiz;
import com.masters.edu.backend.domain.quiz.QuizSubmission;
import com.masters.edu.backend.security.UserPrincipal;
import com.masters.edu.backend.service.quiz.QuizService;
import com.masters.edu.backend.web.dto.quiz.CreateQuizRequest;
import com.masters.edu.backend.web.dto.quiz.QuizDto;
import com.masters.edu.backend.web.dto.quiz.QuizSubmissionDto;
import com.masters.edu.backend.web.dto.quiz.StartQuizRequest;
import com.masters.edu.backend.web.dto.quiz.SubmitQuizRequest;
import com.masters.edu.backend.web.mapper.QuizMapper;

import jakarta.validation.Valid;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api")
public class QuizController {

    private final QuizService quizService;
    private final QuizMapper quizMapper;

    public QuizController(QuizService quizService, QuizMapper quizMapper) {
        this.quizService = quizService;
        this.quizMapper = quizMapper;
    }

    @GetMapping("/courses/{courseId}/quizzes")
    public ResponseEntity<List<QuizDto>> listCourseQuizzes(@PathVariable Long courseId) {
        List<Quiz> quizzes = quizService.quizzesForCourse(courseId);
        return ResponseEntity.ok(quizMapper.toDtos(quizzes));
    }

    @GetMapping("/quizzes/{quizId}")
    public ResponseEntity<QuizDto> getQuiz(@PathVariable Long quizId) {
        Quiz quiz = quizService.getQuizWithQuestions(quizId);
        return ResponseEntity.ok(quizMapper.toDto(quiz));
    }

    @PostMapping("/courses/{courseId}/quizzes")
    @PreAuthorize("hasRole('TEACHER') or hasRole('ADMIN')")
    public ResponseEntity<QuizDto> createQuiz(@PathVariable Long courseId,
            @RequestParam(required = false) Long moduleId,
            @Valid @RequestBody CreateQuizRequest request) {
        Quiz quiz = quizService.createQuiz(courseId, request, moduleId);
        return ResponseEntity.status(HttpStatus.CREATED).body(quizMapper.toDto(quiz));
    }

    @PostMapping("/quizzes/{quizId}/attempts")
    @PreAuthorize("hasRole('STUDENT') or hasRole('ADMIN')")
    public ResponseEntity<QuizSubmissionDto> startAttempt(@PathVariable Long quizId,
            @AuthenticationPrincipal UserPrincipal principal,
            @RequestBody(required = false) StartQuizRequest request) {
        Long studentId = resolveStudentId(principal, request != null ? request.studentId() : null);
        QuizSubmission submission = quizService.startAttempt(quizId, studentId);
        return ResponseEntity.status(HttpStatus.CREATED).body(quizMapper.toSubmissionDto(submission));
    }

    @PostMapping("/quiz-submissions/{submissionId}/submit")
    @PreAuthorize("hasRole('STUDENT') or hasRole('ADMIN')")
    public ResponseEntity<QuizSubmissionDto> submitAttempt(@PathVariable Long submissionId,
            @AuthenticationPrincipal UserPrincipal principal,
            @Valid @RequestBody SubmitQuizRequest request) {
        QuizSubmission submission = quizService.getSubmission(submissionId);
        ensureSubmissionAccess(submission, principal);
        QuizSubmission updated = quizService.submitAttempt(submissionId, request);
        return ResponseEntity.ok(quizMapper.toSubmissionDto(updated));
    }

    @GetMapping("/quizzes/{quizId}/submissions")
    @PreAuthorize("hasRole('TEACHER') or hasRole('ADMIN')")
    public ResponseEntity<List<QuizSubmissionDto>> submissionsForQuiz(@PathVariable Long quizId) {
        List<QuizSubmission> submissions = quizService.submissionsForQuiz(quizId);
        return ResponseEntity.ok(quizMapper.toSubmissionDtos(submissions));
    }

    @GetMapping("/students/{studentId}/quiz-submissions")
    @PreAuthorize("hasRole('STUDENT') or hasRole('ADMIN')")
    public ResponseEntity<List<QuizSubmissionDto>> submissionsForStudent(@PathVariable Long studentId,
            @AuthenticationPrincipal UserPrincipal principal) {
        ensureStudentAccess(studentId, principal);
        List<QuizSubmission> submissions = quizService.submissionsForStudent(studentId);
        return ResponseEntity.ok(quizMapper.toSubmissionDtos(submissions));
    }

    private Long resolveStudentId(UserPrincipal principal, Long requestedStudentId) {
        if (principal == null) {
            throw new AccessDeniedException("Authentication required");
        }
        if (principal.getRole().equals("STUDENT")) {
            if (requestedStudentId != null && !principal.getId().equals(requestedStudentId)) {
                throw new AccessDeniedException("Cannot start attempt for another student");
            }
            return principal.getId();
        }
        if (requestedStudentId == null) {
            throw new AccessDeniedException("Student id is required for this operation");
        }
        return requestedStudentId;
    }

    private void ensureStudentAccess(Long studentId, UserPrincipal principal) {
        if (principal == null) {
            throw new AccessDeniedException("Authentication required");
        }
        if (principal.getRole().equals("STUDENT") && !principal.getId().equals(studentId)) {
            throw new AccessDeniedException("Cannot view submissions of another student");
        }
    }

    private void ensureSubmissionAccess(QuizSubmission submission, UserPrincipal principal) {
        if (principal == null) {
            throw new AccessDeniedException("Authentication required");
        }
        if (principal.getRole().equals("STUDENT") && !principal.getId().equals(submission.getStudent().getId())) {
            throw new AccessDeniedException("Cannot submit attempt for another student");
        }
    }
}


