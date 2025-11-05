-- Demo reference data for local development and integration tests

INSERT INTO users (id, email, password_hash, full_name, role, status, time_zone, created_at, updated_at)
VALUES
    (1, 'admin@example.com', '$2a$10$7EqJtq98hPqEX7fNZaFWoOhi5.Zi9UV38DS1rcQIL1t6f.D9KCp2K', 'Admin User', 'ADMIN', 'ACTIVE', 'UTC', CURRENT_TIMESTAMP, CURRENT_TIMESTAMP),
    (2, 'teacher@example.com', '$2a$10$7EqJtq98hPqEX7fNZaFWoOhi5.Zi9UV38DS1rcQIL1t6f.D9KCp2K', 'Taylor Teacher', 'TEACHER', 'ACTIVE', 'UTC', CURRENT_TIMESTAMP, CURRENT_TIMESTAMP),
    (3, 'student@example.com', '$2a$10$7EqJtq98hPqEX7fNZaFWoOhi5.Zi9UV38DS1rcQIL1t6f.D9KCp2K', 'Sam Student', 'STUDENT', 'ACTIVE', 'UTC', CURRENT_TIMESTAMP, CURRENT_TIMESTAMP);

INSERT INTO profiles (id, user_id, bio, avatar_url, headline, social_links, preferences, created_at, updated_at)
VALUES
    (1, 2, 'Experienced software engineer and instructor.', 'https://example.com/avatar/teacher.png', 'Lead Instructor', '{"linkedin": "https://linkedin.com/in/teacher"}', NULL, CURRENT_TIMESTAMP, CURRENT_TIMESTAMP),
    (2, 3, 'Aspiring backend developer.', NULL, 'Student', NULL, NULL, CURRENT_TIMESTAMP, CURRENT_TIMESTAMP);

INSERT INTO categories (id, name, slug, description, icon, created_at, updated_at)
VALUES
    (1, 'Programming', 'programming', 'Courses focused on software development skills.', 'code', CURRENT_TIMESTAMP, CURRENT_TIMESTAMP);

INSERT INTO tags (id, name, slug, description, created_at, updated_at)
VALUES
    (1, 'Java', 'java', 'Java programming language', CURRENT_TIMESTAMP, CURRENT_TIMESTAMP),
    (2, 'Spring Boot', 'spring-boot', 'Spring Boot framework', CURRENT_TIMESTAMP, CURRENT_TIMESTAMP);

INSERT INTO courses (id, title, slug, summary, description, difficulty_level, estimated_duration, language, published_at, status, category_id, teacher_id, created_at, updated_at)
VALUES
    (1, 'Spring Boot Backend Fundamentals', 'spring-boot-backend', 'Build robust education platforms with Spring Boot.',
     'In this course students learn to design data models, expose REST APIs, and secure applications with Spring Security.',
     'INTERMEDIATE', '6 weeks', 'EN', '2024-01-01T00:00:00Z', 'PUBLISHED', 1, 2, CURRENT_TIMESTAMP, CURRENT_TIMESTAMP);

INSERT INTO modules (id, title, description, order_index, unlock_date, is_mandatory, course_id, created_at, updated_at)
VALUES
    (1, 'Getting Started', 'Set up the project and understand the domain.', 1, '2024-01-02T00:00:00Z', TRUE, 1, CURRENT_TIMESTAMP, CURRENT_TIMESTAMP);

INSERT INTO lessons (id, title, summary, content, video_url, duration_minutes, order_index, module_id, created_at, updated_at)
VALUES
    (1, 'Domain Modeling with JPA', 'Designing entities and relationships.',
     'We explore entity design, relationships, and best practices for lazy loading.',
     'https://videos.example.com/jpa-intro', 35, 1, 1, CURRENT_TIMESTAMP, CURRENT_TIMESTAMP);

INSERT INTO lesson_resources (id, title, resource_type, url, file_path, order_index, lesson_id, metadata, created_at, updated_at)
VALUES
    (1, 'Entity Relationship Diagram', 'PDF', 'https://files.example.com/erd.pdf', NULL, 1, 1, '{"size":"1MB"}'::jsonb, CURRENT_TIMESTAMP, CURRENT_TIMESTAMP);

INSERT INTO assignments (id, title, description, due_at, max_score, submission_type, grading_type, status, order_index, release_at,
                         lesson_id, creator_id, allow_late_submission, late_penalty_percentage, lock_after_deadline,
                         discussion_enabled, grading_deadline, created_at, updated_at)
VALUES
    (1, 'Design the Course Schema', 'Model the full course management schema with JPA entities.',
     '2024-01-14T23:59:59Z', 100, 'TEXT', 'NUMERIC', 'PUBLISHED', 1, '2024-01-07T00:00:00Z',
     1, 2, FALSE, NULL, FALSE, TRUE, '2024-01-21T23:59:59Z', CURRENT_TIMESTAMP, CURRENT_TIMESTAMP);

INSERT INTO enrollments (id, enrolled_at, status, progress_percentage, last_access_at, student_id, course_id, created_at, updated_at)
VALUES
    (1, '2024-01-05T10:00:00Z', 'ACTIVE', 25.00, '2024-01-15T13:00:00Z', 3, 1, CURRENT_TIMESTAMP, CURRENT_TIMESTAMP);

INSERT INTO course_reviews (id, rating, comment, is_public, course_id, student_id, enrollment_id, created_at, updated_at)
VALUES
    (1, 5, 'Fantastic course with hands-on examples.', TRUE, 1, 3, 1, CURRENT_TIMESTAMP, CURRENT_TIMESTAMP);

INSERT INTO announcements (id, title, content, published_at, visible_until, course_id, author_id, created_at, updated_at)
VALUES
    (1, 'Live Q&A Session', 'Join us for a live Q&A session on Friday.', '2024-01-10T17:00:00Z', '2024-01-12T17:00:00Z', 1, 2, CURRENT_TIMESTAMP, CURRENT_TIMESTAMP);

INSERT INTO course_tags (id, course_id, tag_id, created_by, weight, created_at, updated_at)
VALUES
    (1, 1, 1, 2, 10, CURRENT_TIMESTAMP, CURRENT_TIMESTAMP),
    (2, 1, 2, 2, 9, CURRENT_TIMESTAMP, CURRENT_TIMESTAMP);

INSERT INTO quizzes (id, title, description, total_points, time_limit_seconds, attempt_limit, grading_method, quiz_type, module_id, course_id, created_at, updated_at)
VALUES
    (1, 'Module 1 Checkpoint', 'Validate understanding of domain modeling.', 100, 1800, 3, 'HIGHEST', 'MODULE_FINAL', 1, 1, CURRENT_TIMESTAMP, CURRENT_TIMESTAMP);

INSERT INTO quiz_questions (id, text, explanation, points, question_type, order_index, quiz_id, created_at, updated_at)
VALUES
    (1, 'Which annotation configures a lazy-loaded one-to-many relationship?',
     'Remember to specify fetch strategy when deviating from defaults.', 10, 'SINGLE_CHOICE', 1, 1, CURRENT_TIMESTAMP, CURRENT_TIMESTAMP);

INSERT INTO answer_options (id, text, is_correct, feedback, order_index, question_id, created_at, updated_at)
VALUES
    (1, '@OneToMany(mappedBy = "field", fetch = FetchType.LAZY)', TRUE, 'Correct! Fetch type is explicitly set to LAZY.', 1, 1, CURRENT_TIMESTAMP, CURRENT_TIMESTAMP),
    (2, '@ManyToOne(fetch = FetchType.EAGER)', FALSE, 'Many-to-one is eager by default unless overridden.', 2, 1, CURRENT_TIMESTAMP, CURRENT_TIMESTAMP);

INSERT INTO submissions (id, assignment_id, student_id, grader_id, status, submitted_at, content, attachment_path, score, feedback,
                         graded_at, graded_by, feedback_updated_at, created_at, updated_at)
VALUES
    (1, 1, 3, 2, 'GRADED', '2024-01-13T20:00:00Z', 'Attached ER diagram and entity definitions.', NULL, 95,
     'Excellent structure and naming conventions.', '2024-01-15T09:30:00Z', 2, '2024-01-15T09:30:00Z', CURRENT_TIMESTAMP, CURRENT_TIMESTAMP);

INSERT INTO quiz_submissions (id, quiz_id, student_id, enrollment_id, status, started_at, completed_at, score, attempt_number, created_at, updated_at)
VALUES
    (1, 1, 3, 1, 'GRADED', '2024-01-11T18:00:00Z', '2024-01-11T18:12:00Z', 90, 1, CURRENT_TIMESTAMP, CURRENT_TIMESTAMP);

INSERT INTO quiz_submission_answers (id, quiz_submission_id, question_id, answer_option_id, answer_text, is_correct, awarded_points, created_at, updated_at)
VALUES
    (1, 1, 1, 1, NULL, TRUE, 10, CURRENT_TIMESTAMP, CURRENT_TIMESTAMP);

SELECT setval('users_id_seq', (SELECT MAX(id) FROM users));
SELECT setval('profiles_id_seq', (SELECT MAX(id) FROM profiles));
SELECT setval('categories_id_seq', (SELECT MAX(id) FROM categories));
SELECT setval('tags_id_seq', (SELECT MAX(id) FROM tags));
SELECT setval('courses_id_seq', (SELECT MAX(id) FROM courses));
SELECT setval('modules_id_seq', (SELECT MAX(id) FROM modules));
SELECT setval('lessons_id_seq', (SELECT MAX(id) FROM lessons));
SELECT setval('lesson_resources_id_seq', (SELECT MAX(id) FROM lesson_resources));
SELECT setval('assignments_id_seq', (SELECT MAX(id) FROM assignments));
SELECT setval('enrollments_id_seq', (SELECT MAX(id) FROM enrollments));
SELECT setval('course_reviews_id_seq', (SELECT MAX(id) FROM course_reviews));
SELECT setval('announcements_id_seq', (SELECT MAX(id) FROM announcements));
SELECT setval('course_tags_id_seq', (SELECT MAX(id) FROM course_tags));
SELECT setval('quizzes_id_seq', (SELECT MAX(id) FROM quizzes));
SELECT setval('quiz_questions_id_seq', (SELECT MAX(id) FROM quiz_questions));
SELECT setval('answer_options_id_seq', (SELECT MAX(id) FROM answer_options));
SELECT setval('submissions_id_seq', (SELECT MAX(id) FROM submissions));
SELECT setval('quiz_submissions_id_seq', (SELECT MAX(id) FROM quiz_submissions));
SELECT setval('quiz_submission_answers_id_seq', (SELECT MAX(id) FROM quiz_submission_answers));


