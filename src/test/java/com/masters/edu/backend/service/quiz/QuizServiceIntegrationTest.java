package com.masters.edu.backend.service.quiz;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

import java.util.List;

import com.masters.edu.backend.IntegrationTestSupport;
import com.masters.edu.backend.domain.quiz.QuizSubmission;
import com.masters.edu.backend.domain.quiz.QuizSubmissionAnswer;
import com.masters.edu.backend.domain.quiz.QuizSubmissionStatus;
import com.masters.edu.backend.domain.user.User;
import com.masters.edu.backend.domain.user.UserRole;
import com.masters.edu.backend.domain.user.UserStatus;
import com.masters.edu.backend.repository.user.UserRepository;
import com.masters.edu.backend.web.dto.quiz.QuizAnswerRequest;
import com.masters.edu.backend.web.dto.quiz.SubmitQuizRequest;

import jakarta.validation.ValidationException;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;

class QuizServiceIntegrationTest extends IntegrationTestSupport {

    @Autowired
    private QuizService quizService;

    @Autowired
    private UserRepository userRepository;

    @Test
    void startAndSubmitAttempt_scoresCorrectly() {
        QuizSubmission submission = quizService.startAttempt(1L, 3L);

        assertThat(submission.getStatus()).isEqualTo(QuizSubmissionStatus.IN_PROGRESS);
        assertThat(submission.getAttemptNumber()).isEqualTo(2);

        SubmitQuizRequest request = new SubmitQuizRequest(null,
                List.of(new QuizAnswerRequest(1L, 1L, null)));

        QuizSubmission completed = quizService.submitAttempt(submission.getId(), request);

        assertThat(completed.getStatus()).isEqualTo(QuizSubmissionStatus.SUBMITTED);
        assertThat(completed.getScore()).isEqualTo(10);
        assertThat(completed.getAnswers()).hasSize(1);

        QuizSubmissionAnswer answer = completed.getAnswers().iterator().next();
        assertThat(answer.getAnswerOption()).isNotNull();
        assertThat(answer.getCorrect()).isTrue();
        assertThat(answer.getAwardedPoints()).isEqualTo(10);

        assertThat(quizService.completedAttempts(1L)).isEqualTo(1);
    }

    @Test
    void startAttempt_exceedsLimit_throwsValidationException() {
        User student = new User();
        student.setEmail("attempt.limit@example.com");
        student.setPasswordHash("secret");
        student.setFullName("Attempt Limit Student");
        student.setRole(UserRole.STUDENT);
        student.setStatus(UserStatus.ACTIVE);
        Long studentId = userRepository.save(student).getId();

        quizService.startAttempt(1L, studentId);
        quizService.startAttempt(1L, studentId);
        quizService.startAttempt(1L, studentId);

        assertThatThrownBy(() -> quizService.startAttempt(1L, studentId))
                .isInstanceOf(ValidationException.class)
                .hasMessageContaining("Attempt limit");
    }
}


