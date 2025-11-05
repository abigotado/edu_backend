package com.masters.edu.backend.service.enrollment;

import java.time.OffsetDateTime;
import java.util.List;

import com.masters.edu.backend.domain.course.Course;
import com.masters.edu.backend.domain.enrollment.Enrollment;
import com.masters.edu.backend.domain.enrollment.EnrollmentStatus;
import com.masters.edu.backend.domain.user.User;
import com.masters.edu.backend.repository.enrollment.EnrollmentRepository;
import com.masters.edu.backend.repository.course.CourseRepository;
import com.masters.edu.backend.repository.user.UserRepository;

import jakarta.persistence.EntityNotFoundException;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@Transactional
public class EnrollmentService {

    private final EnrollmentRepository enrollmentRepository;
    private final CourseRepository courseRepository;
    private final UserRepository userRepository;

    public EnrollmentService(EnrollmentRepository enrollmentRepository,
            CourseRepository courseRepository,
            UserRepository userRepository) {
        this.enrollmentRepository = enrollmentRepository;
        this.courseRepository = courseRepository;
        this.userRepository = userRepository;
    }

    public Enrollment enrollStudent(Long courseId, Long studentId) {
        Course course = courseRepository.findById(courseId)
                .orElseThrow(() -> new EntityNotFoundException("Course not found: " + courseId));
        User student = userRepository.findById(studentId)
                .orElseThrow(() -> new EntityNotFoundException("Student not found: " + studentId));

        return enrollmentRepository.findByCourseIdAndStudentId(courseId, studentId)
                .map(enrollment -> reactivateIfNeeded(enrollment))
                .orElseGet(() -> createEnrollment(course, student));
    }

    private Enrollment createEnrollment(Course course, User student) {
        Enrollment enrollment = new Enrollment();
        enrollment.setCourse(course);
        enrollment.setStudent(student);
        enrollment.setStatus(EnrollmentStatus.ACTIVE);
        enrollment.setEnrolledAt(OffsetDateTime.now());
        return enrollmentRepository.save(enrollment);
    }

    private Enrollment reactivateIfNeeded(Enrollment enrollment) {
        if (enrollment.getStatus() == EnrollmentStatus.WITHDRAWN) {
            enrollment.setStatus(EnrollmentStatus.ACTIVE);
            enrollment.setEnrolledAt(OffsetDateTime.now());
            return enrollmentRepository.save(enrollment);
        }
        throw new jakarta.validation.ValidationException("Student already enrolled in this course");
    }

    public List<Enrollment> enrollmentsForStudent(Long studentId, EnrollmentStatus status) {
        return enrollmentRepository.findByStudentIdAndStatus(studentId, status);
    }

    public List<Enrollment> enrollmentsForStudent(Long studentId) {
        return enrollmentRepository.findByStudentId(studentId);
    }

    public List<Enrollment> enrollmentsForCourse(Long courseId) {
        return enrollmentRepository.findByCourseId(courseId);
    }

    public void updateStatus(Long enrollmentId, EnrollmentStatus status) {
        int updated = enrollmentRepository.updateStatus(enrollmentId, status);
        if (updated == 0) {
            throw new EntityNotFoundException("Enrollment not found: " + enrollmentId);
        }
    }

    public long activeCount(Long courseId) {
        return enrollmentRepository.countByCourseAndStatus(courseId, EnrollmentStatus.ACTIVE);
    }

    public void unenroll(Long enrollmentId) {
        Enrollment enrollment = enrollmentRepository.findById(enrollmentId)
                .orElseThrow(() -> new EntityNotFoundException("Enrollment not found: " + enrollmentId));
        enrollment.setStatus(EnrollmentStatus.WITHDRAWN);
        enrollment.setLastAccessAt(OffsetDateTime.now());
        enrollmentRepository.save(enrollment);
    }
}


