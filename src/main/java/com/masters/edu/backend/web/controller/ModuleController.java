package com.masters.edu.backend.web.controller;

import java.util.List;

import com.masters.edu.backend.domain.lesson.Module;
import com.masters.edu.backend.service.course.ModuleService;
import com.masters.edu.backend.web.dto.course.CreateModuleRequest;
import com.masters.edu.backend.web.dto.course.ModuleDto;
import com.masters.edu.backend.web.mapper.CourseMapper;

import jakarta.validation.Valid;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/courses/{courseId}/modules")
public class ModuleController {

    private final ModuleService moduleService;
    private final CourseMapper courseMapper;

    public ModuleController(ModuleService moduleService, CourseMapper courseMapper) {
        this.moduleService = moduleService;
        this.courseMapper = courseMapper;
    }

    @GetMapping
    public ResponseEntity<List<ModuleDto>> listModules(@PathVariable Long courseId) {
        List<Module> modules = moduleService.modulesForCourse(courseId);
        return ResponseEntity.ok(courseMapper.toModuleDtos(modules));
    }

    @PostMapping
    @PreAuthorize("hasRole('TEACHER') or hasRole('ADMIN')")
    public ResponseEntity<ModuleDto> createModule(@PathVariable Long courseId,
            @Valid @RequestBody CreateModuleRequest request) {
        Module module = moduleService.createModule(courseId, request);
        return ResponseEntity.status(HttpStatus.CREATED).body(courseMapper.toModuleDto(module));
    }
}


