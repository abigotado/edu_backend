package com.masters.edu.backend.web.controller;

import static org.assertj.core.api.Assertions.assertThat;
import static org.springframework.http.MediaType.APPLICATION_JSON;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.masters.edu.backend.IntegrationTestSupport;
import com.masters.edu.backend.domain.enrollment.Enrollment;
import com.masters.edu.backend.domain.enrollment.EnrollmentStatus;
import com.masters.edu.backend.domain.user.User;
import com.masters.edu.backend.domain.user.UserRole;
import com.masters.edu.backend.domain.user.UserStatus;
import com.masters.edu.backend.repository.enrollment.EnrollmentRepository;
import com.masters.edu.backend.repository.user.UserRepository;
import com.masters.edu.backend.web.dto.enrollment.EnrollRequest;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.test.web.servlet.MockMvc;

@AutoConfigureMockMvc
class EnrollmentControllerIntegrationTest extends IntegrationTestSupport {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private ObjectMapper objectMapper;

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private EnrollmentRepository enrollmentRepository;

    @Autowired
    private PasswordEncoder passwordEncoder;

    @Test
    @WithMockUser(roles = {"STUDENT"})
    void listStudentEnrollments_returnsSeededEnrollment() throws Exception {
        mockMvc.perform(get("/api/students/{studentId}/enrollments", 3L))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$", org.hamcrest.Matchers.not(org.hamcrest.Matchers.empty())))
                .andExpect(jsonPath("$[0].studentId").value(3))
                .andExpect(jsonPath("$[0].courseTitle").value("Spring Boot Backend Fundamentals"))
                .andExpect(jsonPath("$[0].status").value("ACTIVE"));
    }

    @Test
    @WithMockUser(username = "student@example.com", roles = {"STUDENT"})
    void enroll_createsEnrollmentForStudent() throws Exception {
        User newStudent = new User();
        newStudent.setEmail("integration.student@example.com");
        newStudent.setPasswordHash(passwordEncoder.encode("secret"));
        newStudent.setFullName("Integration Student");
        newStudent.setRole(UserRole.STUDENT);
        newStudent.setStatus(UserStatus.ACTIVE);
        User persistedStudent = userRepository.save(newStudent);

        mockMvc.perform(post("/api/courses/{courseId}/enrollments", 1L)
                .contentType(APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(new EnrollRequest(persistedStudent.getId()))))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.studentId").value(persistedStudent.getId()))
                .andExpect(jsonPath("$.status").value("ACTIVE"));

        Enrollment enrollment = enrollmentRepository.findByCourseIdAndStudentId(1L, persistedStudent.getId())
                .orElseThrow();
        assertThat(enrollment.getStatus()).isEqualTo(EnrollmentStatus.ACTIVE);
    }

    @Test
    @WithMockUser(username = "student@example.com", roles = {"STUDENT"})
    void enroll_duplicateEnrollment_returnsBadRequest() throws Exception {
        User student = new User();
        student.setEmail("duplicate.student@example.com");
        student.setPasswordHash(passwordEncoder.encode("secret"));
        student.setFullName("Duplicate Student");
        student.setRole(UserRole.STUDENT);
        student.setStatus(UserStatus.ACTIVE);
        Long studentId = userRepository.save(student).getId();

        EnrollRequest request = new EnrollRequest(studentId);

        mockMvc.perform(post("/api/courses/{courseId}/enrollments", 1L)
                .contentType(APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isCreated());

        mockMvc.perform(post("/api/courses/{courseId}/enrollments", 1L)
                .contentType(APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.message").value("Student already enrolled in this course"));
    }
}


