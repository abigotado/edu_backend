package com.masters.edu.backend.service.quiz;

import java.time.OffsetDateTime;
import java.util.List;

import com.masters.edu.backend.domain.quiz.Quiz;
import com.masters.edu.backend.domain.quiz.QuizSubmission;
import com.masters.edu.backend.domain.quiz.QuizSubmissionStatus;
import com.masters.edu.backend.domain.user.User;
import com.masters.edu.backend.repository.quiz.QuizRepository;
import com.masters.edu.backend.repository.quiz.QuizSubmissionRepository;
import com.masters.edu.backend.repository.user.UserRepository;

import jakarta.persistence.EntityNotFoundException;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@Transactional
public class QuizService {

    private final QuizRepository quizRepository;
    private final QuizSubmissionRepository submissionRepository;
    private final UserRepository userRepository;

    public QuizService(QuizRepository quizRepository,
            QuizSubmissionRepository submissionRepository,
            UserRepository userRepository) {
        this.quizRepository = quizRepository;
        this.submissionRepository = submissionRepository;
        this.userRepository = userRepository;
    }

    public Quiz getQuiz(Long quizId) {
        return quizRepository.findById(quizId)
                .orElseThrow(() -> new EntityNotFoundException("Quiz not found: " + quizId));
    }

    public Quiz getQuizWithQuestions(Long quizId) {
        return quizRepository.findDetailedById(quizId);
    }

    public List<Quiz> quizzesForCourse(Long courseId) {
        return quizRepository.findByCourseId(courseId);
    }

    public QuizSubmission startAttempt(Long quizId, Long studentId) {
        Quiz quiz = getQuiz(quizId);
        User student = userRepository.findById(studentId)
                .orElseThrow(() -> new EntityNotFoundException("Student not found: " + studentId));

        QuizSubmission submission = new QuizSubmission();
        submission.setQuiz(quiz);
        submission.setStudent(student);
        submission.setStatus(QuizSubmissionStatus.IN_PROGRESS);
        submission.setStartedAt(OffsetDateTime.now());
        submission.setAttemptNumber(calculateAttemptNumber(quizId, studentId));

        return submissionRepository.save(submission);
    }

    private int calculateAttemptNumber(Long quizId, Long studentId) {
        return (int) submissionRepository.findByQuizIdAndStudentId(quizId, studentId).stream()
                .mapToInt(QuizSubmission::getAttemptNumber)
                .max()
                .orElse(0) + 1;
    }

    public QuizSubmission submitAttempt(Long submissionId, Integer score) {
        QuizSubmission submission = submissionRepository.findWithAnswersById(submissionId)
                .orElseThrow(() -> new EntityNotFoundException("Quiz submission not found: " + submissionId));
        submission.setScore(score);
        submission.setStatus(QuizSubmissionStatus.SUBMITTED);
        submission.setCompletedAt(OffsetDateTime.now());
        return submissionRepository.save(submission);
    }

    public long completedAttempts(Long quizId) {
        return submissionRepository.countByQuizIdAndStatus(quizId, QuizSubmissionStatus.SUBMITTED);
    }
}


