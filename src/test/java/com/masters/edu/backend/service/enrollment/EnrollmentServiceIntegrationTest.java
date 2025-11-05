package com.masters.edu.backend.service.enrollment;

import static org.assertj.core.api.Assertions.assertThat;

import com.masters.edu.backend.IntegrationTestSupport;
import com.masters.edu.backend.domain.enrollment.Enrollment;
import com.masters.edu.backend.domain.enrollment.EnrollmentStatus;
import com.masters.edu.backend.repository.enrollment.EnrollmentRepository;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;

class EnrollmentServiceIntegrationTest extends IntegrationTestSupport {

    @Autowired
    private EnrollmentService enrollmentService;

    @Autowired
    private EnrollmentRepository enrollmentRepository;

    @Test
    void unenroll_updatesStatusToWithdrawn() {
        enrollmentService.unenroll(1L);

        Enrollment enrollment = enrollmentRepository.findById(1L).orElseThrow();
        assertThat(enrollment.getStatus()).isEqualTo(EnrollmentStatus.WITHDRAWN);
        assertThat(enrollment.getLastAccessAt()).isNotNull();
    }

    @Test
    void activeCount_returnsNumberOfActiveStudents() {
        long count = enrollmentService.activeCount(1L);
        assertThat(count).isEqualTo(1);
    }
}


