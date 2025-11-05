package com.masters.edu.backend.service.course;

import java.util.List;

import com.masters.edu.backend.domain.lesson.Lesson;
import com.masters.edu.backend.domain.lesson.Module;
import com.masters.edu.backend.repository.lesson.LessonRepository;
import com.masters.edu.backend.repository.lesson.ModuleRepository;
import com.masters.edu.backend.web.dto.course.CreateLessonRequest;

import jakarta.persistence.EntityNotFoundException;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@Transactional
public class LessonService {

    private final LessonRepository lessonRepository;
    private final ModuleRepository moduleRepository;

    public LessonService(LessonRepository lessonRepository, ModuleRepository moduleRepository) {
        this.lessonRepository = lessonRepository;
        this.moduleRepository = moduleRepository;
    }

    @Transactional(readOnly = true)
    public List<Lesson> lessonsForModule(Long moduleId) {
        return lessonRepository.findByModuleIdOrderByOrderIndexAsc(moduleId);
    }

    public Lesson createLesson(Long moduleId, CreateLessonRequest request) {
        Module module = moduleRepository.findById(moduleId)
                .orElseThrow(() -> new EntityNotFoundException("Module not found: " + moduleId));

        Lesson lesson = new Lesson();
        lesson.setModule(module);
        lesson.setTitle(request.title());
        lesson.setSummary(request.summary());
        lesson.setContent(request.content());
        lesson.setVideoUrl(request.videoUrl());
        lesson.setDurationMinutes(request.durationMinutes());
        lesson.setOrderIndex(request.orderIndex());
        return lessonRepository.save(lesson);
    }
}


