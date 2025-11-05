package com.masters.edu.backend.service.quiz;

import java.time.OffsetDateTime;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Objects;

import com.masters.edu.backend.domain.course.Course;
import com.masters.edu.backend.domain.lesson.Module;
import com.masters.edu.backend.domain.quiz.AnswerOption;
import com.masters.edu.backend.domain.quiz.Question;
import com.masters.edu.backend.domain.quiz.QuestionType;
import com.masters.edu.backend.domain.quiz.Quiz;
import com.masters.edu.backend.domain.quiz.QuizSubmission;
import com.masters.edu.backend.domain.quiz.QuizSubmissionAnswer;
import com.masters.edu.backend.domain.quiz.QuizSubmissionStatus;
import com.masters.edu.backend.domain.user.User;
import com.masters.edu.backend.repository.course.CourseRepository;
import com.masters.edu.backend.repository.lesson.ModuleRepository;
import com.masters.edu.backend.repository.quiz.AnswerOptionRepository;
import com.masters.edu.backend.repository.quiz.QuestionRepository;
import com.masters.edu.backend.repository.quiz.QuizRepository;
import com.masters.edu.backend.repository.quiz.QuizSubmissionRepository;
import com.masters.edu.backend.repository.user.UserRepository;
import com.masters.edu.backend.web.dto.quiz.CreateAnswerOptionRequest;
import com.masters.edu.backend.web.dto.quiz.CreateQuestionRequest;
import com.masters.edu.backend.web.dto.quiz.CreateQuizRequest;
import com.masters.edu.backend.web.dto.quiz.QuizAnswerRequest;
import com.masters.edu.backend.web.dto.quiz.SubmitQuizRequest;

import jakarta.persistence.EntityNotFoundException;
import jakarta.validation.ValidationException;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@Transactional
public class QuizService {

    private final QuizRepository quizRepository;
    private final QuizSubmissionRepository submissionRepository;
    private final UserRepository userRepository;
    private final CourseRepository courseRepository;
    private final ModuleRepository moduleRepository;
    private final QuestionRepository questionRepository;
    private final AnswerOptionRepository answerOptionRepository;

    public QuizService(QuizRepository quizRepository,
            QuizSubmissionRepository submissionRepository,
            UserRepository userRepository,
            CourseRepository courseRepository,
            ModuleRepository moduleRepository,
            QuestionRepository questionRepository,
            AnswerOptionRepository answerOptionRepository) {
        this.quizRepository = quizRepository;
        this.submissionRepository = submissionRepository;
        this.userRepository = userRepository;
        this.courseRepository = courseRepository;
        this.moduleRepository = moduleRepository;
        this.questionRepository = questionRepository;
        this.answerOptionRepository = answerOptionRepository;
    }

    public Quiz getQuiz(Long quizId) {
        return quizRepository.findById(quizId)
                .orElseThrow(() -> new EntityNotFoundException("Quiz not found: " + quizId));
    }

    public Quiz getQuizWithQuestions(Long quizId) {
        Quiz quiz = quizRepository.findDetailedById(quizId);
        if (quiz == null) {
            throw new EntityNotFoundException("Quiz not found: " + quizId);
        }
        return quiz;
    }

    public List<Quiz> quizzesForCourse(Long courseId) {
        return quizRepository.findByCourseId(courseId);
    }

    public Quiz createQuiz(Long courseId, CreateQuizRequest request, Long moduleId) {
        Quiz quiz = new Quiz();

        if (courseId != null) {
            Course course = courseRepository.findById(courseId)
                    .orElseThrow(() -> new EntityNotFoundException("Course not found: " + courseId));
            quiz.setCourse(course);
        }

        if (moduleId != null) {
            Module module = moduleRepository.findById(moduleId)
                    .orElseThrow(() -> new EntityNotFoundException("Module not found: " + moduleId));
            if (quiz.getCourse() == null) {
                quiz.setCourse(module.getCourse());
            } else if (module.getCourse() != null && !module.getCourse().getId().equals(quiz.getCourse().getId())) {
                throw new ValidationException("Module does not belong to the specified course");
            }
            quiz.setModule(module);
        }

        quiz.setTitle(request.title());
        quiz.setDescription(request.description());
        quiz.setTotalPoints(request.totalPoints());
        quiz.setTimeLimitSeconds(request.timeLimitSeconds());
        quiz.setAttemptLimit(request.attemptLimit());
        quiz.setGradingMethod(request.gradingMethod());
        quiz.setQuizType(request.quizType());

        if (request.questions() != null) {
            quiz.getQuestions().clear();
            for (CreateQuestionRequest questionRequest : request.questions()) {
                Question question = mapQuestion(questionRequest);
                question.setQuiz(quiz);
                quiz.getQuestions().add(question);
            }
        }

        return quizRepository.save(quiz);
    }

    private Question mapQuestion(CreateQuestionRequest questionRequest) {
        Question question = new Question();
        question.setText(questionRequest.text());
        question.setExplanation(questionRequest.explanation());
        question.setPoints(questionRequest.points());
        question.setQuestionType(questionRequest.questionType());
        question.setOrderIndex(questionRequest.orderIndex());

        if (questionRequest.options() != null) {
            for (CreateAnswerOptionRequest optionRequest : questionRequest.options()) {
                AnswerOption option = new AnswerOption();
                option.setText(optionRequest.text());
                option.setCorrect(optionRequest.correct());
                option.setFeedback(optionRequest.feedback());
                option.setOrderIndex(optionRequest.orderIndex());
                option.setQuestion(question);
                question.getOptions().add(option);
            }
        }
        return question;
    }

    public QuizSubmission startAttempt(Long quizId, Long studentId) {
        Quiz quiz = getQuiz(quizId);
        User student = userRepository.findById(studentId)
                .orElseThrow(() -> new EntityNotFoundException("Student not found: " + studentId));

        List<QuizSubmission> previousAttempts = submissionRepository.findByQuizIdAndStudentId(quizId, studentId);
        if (quiz.getAttemptLimit() != null && previousAttempts.size() >= quiz.getAttemptLimit()) {
            throw new ValidationException("Attempt limit reached for this quiz");
        }

        QuizSubmission submission = new QuizSubmission();
        submission.setQuiz(quiz);
        submission.setStudent(student);
        submission.setStatus(QuizSubmissionStatus.IN_PROGRESS);
        submission.setStartedAt(OffsetDateTime.now());
        submission.setAttemptNumber(previousAttempts.size() + 1);

        return submissionRepository.save(submission);
    }

    public QuizSubmission submitAttempt(Long submissionId, SubmitQuizRequest request) {
        QuizSubmission submission = submissionRepository.findWithAnswersById(submissionId)
                .orElseThrow(() -> new EntityNotFoundException("Quiz submission not found: " + submissionId));
        if (submission.getStatus() != QuizSubmissionStatus.IN_PROGRESS) {
            throw new ValidationException("Submission already completed");
        }
        submission.setStatus(QuizSubmissionStatus.SUBMITTED);
        submission.setCompletedAt(OffsetDateTime.now());

        submission.getAnswers().clear();
        int totalAwarded = 0;
        Map<Long, Question> questionCache = new HashMap<>();

        if (request.answers() != null) {
            for (QuizAnswerRequest answerRequest : request.answers()) {
                Question question = questionCache.computeIfAbsent(answerRequest.questionId(), id -> questionRepository.findById(id)
                        .orElseThrow(() -> new EntityNotFoundException("Question not found: " + id)));
                if (!Objects.equals(question.getQuiz().getId(), submission.getQuiz().getId())) {
                    throw new ValidationException("Question does not belong to this quiz");
                }

                QuizSubmissionAnswer answer = new QuizSubmissionAnswer();
                answer.setQuizSubmission(submission);
                answer.setQuestion(question);
                if (answerRequest.answerOptionId() != null) {
                    AnswerOption option = answerOptionRepository.findById(answerRequest.answerOptionId())
                            .orElseThrow(() -> new EntityNotFoundException("Answer option not found: " + answerRequest.answerOptionId()));
                    if (!Objects.equals(option.getQuestion().getId(), question.getId())) {
                        throw new ValidationException("Answer option does not belong to the question");
                    }
                    answer.setAnswerOption(option);
                    if (question.getQuestionType() != QuestionType.OPEN_ENDED) {
                        boolean correct = option.isCorrect();
                        answer.setCorrect(correct);
                        int awarded = correct ? Objects.requireNonNullElse(question.getPoints(), 0) : 0;
                        answer.setAwardedPoints(awarded);
                        totalAwarded += awarded;
                    }
                } else {
                    answer.setCorrect(null);
                    answer.setAwardedPoints(null);
                }
                answer.setAnswerText(answerRequest.answerText());
                submission.getAnswers().add(answer);
            }
        }

        Integer score = request.score() != null ? request.score() : totalAwarded;
        submission.setScore(score);
        return submissionRepository.save(submission);
    }

    public long completedAttempts(Long quizId) {
        return submissionRepository.countByQuizIdAndStatus(quizId, QuizSubmissionStatus.SUBMITTED);
    }

    @Transactional(readOnly = true)
    public List<QuizSubmission> submissionsForQuiz(Long quizId) {
        return submissionRepository.findByQuizId(quizId);
    }

    @Transactional(readOnly = true)
    public List<QuizSubmission> submissionsForStudent(Long studentId) {
        return submissionRepository.findByStudentId(studentId);
    }

    @Transactional(readOnly = true)
    public QuizSubmission getSubmission(Long submissionId) {
        return submissionRepository.findDetailedById(submissionId)
                .orElseThrow(() -> new EntityNotFoundException("Quiz submission not found: " + submissionId));
    }
}


