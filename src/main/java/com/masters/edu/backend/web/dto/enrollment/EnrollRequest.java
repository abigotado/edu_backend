package com.masters.edu.backend.web.dto.enrollment;

import jakarta.validation.constraints.NotNull;

public record EnrollRequest(@NotNull Long studentId) {
}


