package com.masters.edu.backend.web.mapper;

import java.util.List;

import com.masters.edu.backend.domain.course.Course;
import com.masters.edu.backend.domain.lesson.Module;
import com.masters.edu.backend.web.dto.course.CourseDetailDto;
import com.masters.edu.backend.web.dto.course.CourseSummaryDto;
import com.masters.edu.backend.web.dto.course.ModuleDto;

import org.mapstruct.Mapper;
import org.mapstruct.Mapping;

@Mapper(componentModel = "spring")
public interface CourseMapper {

    @Mapping(target = "teacherName", source = "teacher.fullName")
    @Mapping(target = "categoryName", source = "category.name")
    CourseSummaryDto toSummary(Course course);

    @Mapping(target = "teacherName", source = "teacher.fullName")
    @Mapping(target = "categoryName", source = "category.name")
    @Mapping(target = "modules", source = "modules")
    CourseDetailDto toDetail(Course course);

    @Mapping(target = "orderIndex", source = "orderIndex")
    ModuleDto toModuleDto(Module module);

    List<ModuleDto> toModuleDtos(List<Module> modules);
}


