package com.masters.edu.backend.web.mapper;

import java.util.List;

import com.masters.edu.backend.domain.enrollment.Enrollment;
import com.masters.edu.backend.web.dto.enrollment.EnrollmentDto;

import org.mapstruct.Mapper;
import org.mapstruct.Mapping;

@Mapper(componentModel = "spring")
public interface EnrollmentMapper {

    @Mapping(target = "courseId", source = "course.id")
    @Mapping(target = "courseTitle", source = "course.title")
    @Mapping(target = "studentId", source = "student.id")
    @Mapping(target = "studentName", source = "student.fullName")
    EnrollmentDto toDto(Enrollment enrollment);

    List<EnrollmentDto> toDtos(List<Enrollment> enrollments);
}


