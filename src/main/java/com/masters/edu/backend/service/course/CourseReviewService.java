package com.masters.edu.backend.service.course;

import java.util.List;

import com.masters.edu.backend.domain.course.Course;
import com.masters.edu.backend.domain.course.CourseReview;
import com.masters.edu.backend.domain.user.User;
import com.masters.edu.backend.repository.course.CourseRepository;
import com.masters.edu.backend.repository.course.CourseReviewRepository;
import com.masters.edu.backend.repository.user.UserRepository;

import jakarta.persistence.EntityNotFoundException;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@Transactional
public class CourseReviewService {

    private final CourseReviewRepository reviewRepository;
    private final CourseRepository courseRepository;
    private final UserRepository userRepository;

    public CourseReviewService(CourseReviewRepository reviewRepository,
            CourseRepository courseRepository,
            UserRepository userRepository) {
        this.reviewRepository = reviewRepository;
        this.courseRepository = courseRepository;
        this.userRepository = userRepository;
    }

    public CourseReview addReview(Long courseId, Long studentId, int rating, String comment) {
        Course course = courseRepository.findById(courseId)
                .orElseThrow(() -> new EntityNotFoundException("Course not found: " + courseId));
        User student = userRepository.findById(studentId)
                .orElseThrow(() -> new EntityNotFoundException("Student not found: " + studentId));

        CourseReview review = reviewRepository.findByCourseIdAndStudentId(courseId, studentId)
                .orElse(new CourseReview());

        review.setCourse(course);
        review.setStudent(student);
        review.setRating(rating);
        review.setComment(comment);
        review.setPublicReview(true);
        return reviewRepository.save(review);
    }

    public List<CourseReview> reviewsForCourse(Long courseId) {
        return reviewRepository.findByCourseId(courseId);
    }

    public double averageRating(Long courseId) {
        return reviewRepository.findAverageRatingByCourseId(courseId);
    }
}


