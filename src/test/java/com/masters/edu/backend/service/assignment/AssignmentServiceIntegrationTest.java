package com.masters.edu.backend.service.assignment;

import static org.assertj.core.api.Assertions.assertThat;

import com.masters.edu.backend.IntegrationTestSupport;
import com.masters.edu.backend.domain.assignment.Assignment;
import com.masters.edu.backend.domain.assignment.AssignmentStatus;
import com.masters.edu.backend.domain.assignment.GradingType;
import com.masters.edu.backend.domain.assignment.Submission;
import com.masters.edu.backend.domain.assignment.SubmissionStatus;
import com.masters.edu.backend.domain.assignment.SubmissionType;
import com.masters.edu.backend.repository.assignment.AssignmentRepository;
import com.masters.edu.backend.repository.assignment.SubmissionRepository;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;

class AssignmentServiceIntegrationTest extends IntegrationTestSupport {

    @Autowired
    private AssignmentService assignmentService;

    @Autowired
    private AssignmentRepository assignmentRepository;

    @Autowired
    private SubmissionRepository submissionRepository;

    @Test
    void createAssignment_linksToLessonAndCreator() {
        Assignment assignment = new Assignment();
        assignment.setTitle("Integration Assignment");
        assignment.setDescription("Описание задания");
        assignment.setOrderIndex(2);
        assignment.setMaxScore(100);
        assignment.setSubmissionType(SubmissionType.TEXT);
        assignment.setGradingType(GradingType.NUMERIC);

        Assignment saved = assignmentService.createAssignment(1L, assignment, 2L);

        assertThat(saved.getId()).isNotNull();
        assertThat(saved.getLesson().getId()).isEqualTo(1L);
        assertThat(saved.getCreator().getId()).isEqualTo(2L);
        assertThat(saved.getStatus()).isEqualTo(AssignmentStatus.DRAFT);
    }

    @Test
    void submitAssignment_createsSubmissionForStudent() {
        Assignment assignment = new Assignment();
        assignment.setTitle("Submission Assignment");
        assignment.setOrderIndex(3);
        assignment.setSubmissionType(SubmissionType.TEXT);
        assignment.setGradingType(GradingType.NUMERIC);
        Assignment saved = assignmentService.createAssignment(1L, assignment, 2L);

        Submission request = new Submission();
        request.setContent("Решение студента");

        Submission submission = assignmentService.submit(saved.getId(), 3L, request);

        assertThat(submission.getId()).isNotNull();
        assertThat(submission.getAssignment().getId()).isEqualTo(saved.getId());
        assertThat(submission.getStudent().getId()).isEqualTo(3L);
        assertThat(submission.getStatus()).isEqualTo(SubmissionStatus.SUBMITTED);
    }

    @Test
    void gradeSubmission_setsScoreFeedbackAndAudit() {
        Assignment assignment = new Assignment();
        assignment.setTitle("Grading Assignment");
        assignment.setOrderIndex(4);
        assignment.setSubmissionType(SubmissionType.TEXT);
        assignment.setGradingType(GradingType.NUMERIC);
        Assignment saved = assignmentService.createAssignment(1L, assignment, 2L);

        Submission draft = new Submission();
        draft.setContent("Ответ для проверки");
        Submission submission = assignmentService.submit(saved.getId(), 3L, draft);

        Submission graded = assignmentService.grade(submission.getId(), 2L, 95, "Отличная работа");

        assertThat(graded.getStatus()).isEqualTo(SubmissionStatus.GRADED);
        assertThat(graded.getScore()).isEqualTo(95);
        assertThat(graded.getFeedback()).isEqualTo("Отличная работа");
        assertThat(graded.getAudit()).isNotNull();
        assertThat(graded.getAudit().getGradedById()).isEqualTo(2L);

        assertThat(assignmentRepository.findById(saved.getId())).isPresent();
        assertThat(submissionRepository.findById(submission.getId())).isPresent();
    }
}


