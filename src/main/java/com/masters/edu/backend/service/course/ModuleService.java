package com.masters.edu.backend.service.course;

import java.util.Comparator;
import java.util.List;

import com.masters.edu.backend.domain.course.Course;
import com.masters.edu.backend.domain.lesson.Module;
import com.masters.edu.backend.repository.course.CourseRepository;
import com.masters.edu.backend.repository.lesson.ModuleRepository;
import com.masters.edu.backend.web.dto.course.CreateModuleRequest;

import jakarta.persistence.EntityNotFoundException;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@Transactional
public class ModuleService {

    private final ModuleRepository moduleRepository;
    private final CourseRepository courseRepository;

    public ModuleService(ModuleRepository moduleRepository, CourseRepository courseRepository) {
        this.moduleRepository = moduleRepository;
        this.courseRepository = courseRepository;
    }

    @Transactional(readOnly = true)
    public List<Module> modulesForCourse(Long courseId) {
        List<Module> modules = moduleRepository.findByCourseIdOrderByOrderIndexAsc(courseId);
        modules.sort(Comparator.comparing(Module::getOrderIndex));
        return modules;
    }

    public Module createModule(Long courseId, CreateModuleRequest request) {
        Course course = courseRepository.findById(courseId)
                .orElseThrow(() -> new EntityNotFoundException("Course not found: " + courseId));

        Module module = new Module();
        module.setCourse(course);
        module.setTitle(request.title());
        module.setDescription(request.description());
        module.setOrderIndex(request.orderIndex());
        return moduleRepository.save(module);
    }
}


