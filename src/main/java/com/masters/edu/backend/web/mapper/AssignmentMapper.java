package com.masters.edu.backend.web.mapper;

import java.util.List;

import com.masters.edu.backend.domain.assignment.Assignment;
import com.masters.edu.backend.domain.assignment.Submission;
import com.masters.edu.backend.web.dto.assignment.AssignmentDto;
import com.masters.edu.backend.web.dto.assignment.SubmissionDto;

import org.mapstruct.Mapper;
import org.mapstruct.Mapping;

@Mapper(componentModel = "spring")
public interface AssignmentMapper {

    @Mapping(target = "lessonId", source = "lesson.id")
    @Mapping(target = "lessonTitle", source = "lesson.title")
    @Mapping(target = "allowLateSubmission", source = "settings.allowLateSubmission")
    @Mapping(target = "latePenaltyPercentage", source = "settings.latePenaltyPercentage")
    @Mapping(target = "lockAfterDeadline", source = "settings.lockAfterDeadline")
    @Mapping(target = "discussionEnabled", source = "settings.discussionEnabled")
    @Mapping(target = "gradingDeadline", source = "settings.gradingDeadline")
    AssignmentDto toDto(Assignment assignment);

    List<AssignmentDto> toDtos(List<Assignment> assignments);

    @Mapping(target = "assignmentId", source = "assignment.id")
    @Mapping(target = "assignmentTitle", source = "assignment.title")
    @Mapping(target = "studentId", source = "student.id")
    @Mapping(target = "studentName", source = "student.fullName")
    @Mapping(target = "graderId", source = "grader.id")
    @Mapping(target = "graderName", source = "grader.fullName")
    @Mapping(target = "gradedAt", source = "audit.gradedAt")
    SubmissionDto toSubmissionDto(Submission submission);

    List<SubmissionDto> toSubmissionDtos(List<Submission> submissions);
}


