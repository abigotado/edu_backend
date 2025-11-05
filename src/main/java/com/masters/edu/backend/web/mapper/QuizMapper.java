package com.masters.edu.backend.web.mapper;

import java.util.List;

import com.masters.edu.backend.domain.quiz.AnswerOption;
import com.masters.edu.backend.domain.quiz.Question;
import com.masters.edu.backend.domain.quiz.Quiz;
import com.masters.edu.backend.domain.quiz.QuizSubmission;
import com.masters.edu.backend.web.dto.quiz.AnswerOptionDto;
import com.masters.edu.backend.web.dto.quiz.QuestionDto;
import com.masters.edu.backend.web.dto.quiz.QuizDto;
import com.masters.edu.backend.web.dto.quiz.QuizSubmissionDto;

import org.mapstruct.Mapper;
import org.mapstruct.Mapping;

@Mapper(componentModel = "spring")
public interface QuizMapper {

    @Mapping(target = "courseId", source = "course.id")
    @Mapping(target = "courseTitle", source = "course.title")
    @Mapping(target = "moduleId", source = "module.id")
    @Mapping(target = "moduleTitle", source = "module.title")
    @Mapping(target = "questions", source = "questions")
    QuizDto toDto(Quiz quiz);

    List<QuizDto> toDtos(List<Quiz> quizzes);

    @Mapping(target = "options", source = "options")
    QuestionDto toQuestionDto(Question question);

    List<QuestionDto> toQuestionDtos(List<Question> questions);

    AnswerOptionDto toAnswerOptionDto(AnswerOption option);

    List<AnswerOptionDto> toAnswerOptionDtos(List<AnswerOption> options);

    @Mapping(target = "quizId", source = "quiz.id")
    @Mapping(target = "quizTitle", source = "quiz.title")
    @Mapping(target = "studentId", source = "student.id")
    @Mapping(target = "studentName", source = "student.fullName")
    QuizSubmissionDto toSubmissionDto(QuizSubmission submission);

    List<QuizSubmissionDto> toSubmissionDtos(List<QuizSubmission> submissions);
}


