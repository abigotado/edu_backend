package com.masters.edu.backend.web.dto.enrollment;

import java.math.BigDecimal;
import java.time.OffsetDateTime;

import com.masters.edu.backend.domain.enrollment.EnrollmentStatus;

public record EnrollmentDto(
        Long id,
        Long courseId,
        String courseTitle,
        Long studentId,
        String studentName,
        EnrollmentStatus status,
        BigDecimal progressPercentage,
        OffsetDateTime enrolledAt,
        OffsetDateTime lastAccessAt) {
}


