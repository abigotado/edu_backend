package com.masters.edu.backend.service.assignment;

import java.time.OffsetDateTime;
import java.util.List;

import com.masters.edu.backend.domain.assignment.Assignment;
import com.masters.edu.backend.domain.assignment.AssignmentStatus;
import com.masters.edu.backend.domain.assignment.Submission;
import com.masters.edu.backend.domain.assignment.SubmissionAudit;
import com.masters.edu.backend.domain.assignment.SubmissionStatus;
import com.masters.edu.backend.domain.lesson.Lesson;
import com.masters.edu.backend.domain.user.User;
import com.masters.edu.backend.repository.assignment.AssignmentRepository;
import com.masters.edu.backend.repository.assignment.SubmissionRepository;
import com.masters.edu.backend.repository.lesson.LessonRepository;
import com.masters.edu.backend.repository.user.UserRepository;

import jakarta.persistence.EntityNotFoundException;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@Transactional
public class AssignmentService {

    private final AssignmentRepository assignmentRepository;
    private final SubmissionRepository submissionRepository;
    private final LessonRepository lessonRepository;
    private final UserRepository userRepository;

    public AssignmentService(AssignmentRepository assignmentRepository,
            SubmissionRepository submissionRepository,
            LessonRepository lessonRepository,
            UserRepository userRepository) {
        this.assignmentRepository = assignmentRepository;
        this.submissionRepository = submissionRepository;
        this.lessonRepository = lessonRepository;
        this.userRepository = userRepository;
    }

    public Assignment createAssignment(Long lessonId, Assignment assignment) {
        Lesson lesson = lessonRepository.findById(lessonId)
                .orElseThrow(() -> new EntityNotFoundException("Lesson not found: " + lessonId));
        assignment.setLesson(lesson);
        assignment.setStatus(AssignmentStatus.DRAFT);
        return assignmentRepository.save(assignment);
    }

    public Assignment publish(Long assignmentId) {
        Assignment assignment = assignmentRepository.findById(assignmentId)
                .orElseThrow(() -> new EntityNotFoundException("Assignment not found: " + assignmentId));
        assignment.setStatus(AssignmentStatus.PUBLISHED);
        return assignmentRepository.save(assignment);
    }

    public Submission submit(Long assignmentId, Long studentId, Submission submission) {
        Assignment assignment = assignmentRepository.findById(assignmentId)
                .orElseThrow(() -> new EntityNotFoundException("Assignment not found: " + assignmentId));
        User student = userRepository.findById(studentId)
                .orElseThrow(() -> new EntityNotFoundException("Student not found: " + studentId));

        Submission existing = submissionRepository.findByAssignmentIdAndStudentId(assignmentId, studentId)
                .orElse(null);

        if (existing != null) {
            existing.setContent(submission.getContent());
            existing.setAttachmentPath(submission.getAttachmentPath());
            existing.setSubmittedAt(OffsetDateTime.now());
            existing.setStatus(SubmissionStatus.SUBMITTED);
            return submissionRepository.save(existing);
        }

        submission.setAssignment(assignment);
        submission.setStudent(student);
        submission.setSubmittedAt(OffsetDateTime.now());
        submission.setStatus(SubmissionStatus.SUBMITTED);
        return submissionRepository.save(submission);
    }

    public Submission grade(Long submissionId, Long graderId, Integer score, String feedback) {
        Submission submission = submissionRepository.findById(submissionId)
                .orElseThrow(() -> new EntityNotFoundException("Submission not found: " + submissionId));
        User grader = userRepository.findById(graderId)
                .orElseThrow(() -> new EntityNotFoundException("Grader not found: " + graderId));

        submission.setGrader(grader);
        submission.setScore(score);
        submission.setFeedback(feedback);
        submission.setStatus(SubmissionStatus.GRADED);
        var audit = submission.getAudit();
        if (audit == null) {
            audit = SubmissionAudit.empty();
            submission.setAudit(audit);
        }
        audit.setGradedAt(OffsetDateTime.now());
        audit.setGradedById(grader.getId());
        return submissionRepository.save(submission);
    }

    public List<Assignment> assignmentsForCourse(Long courseId) {
        return assignmentRepository.findByLessonModuleCourseId(courseId);
    }

    public long submittedCount(Long assignmentId) {
        return submissionRepository.countByAssignmentIdAndStatus(assignmentId, SubmissionStatus.SUBMITTED);
    }
}


