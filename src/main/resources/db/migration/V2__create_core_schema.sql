-- Core domain schema for the education platform

CREATE TABLE users (
    id BIGSERIAL PRIMARY KEY,
    email VARCHAR(255) NOT NULL,
    password_hash VARCHAR(255) NOT NULL,
    full_name VARCHAR(255) NOT NULL,
    role VARCHAR(20) NOT NULL,
    status VARCHAR(20) NOT NULL,
    time_zone VARCHAR(50),
    last_login_at TIMESTAMPTZ,
    created_at TIMESTAMPTZ NOT NULL DEFAULT CURRENT_TIMESTAMP,
    updated_at TIMESTAMPTZ NOT NULL DEFAULT CURRENT_TIMESTAMP,
    CONSTRAINT uk_user_email UNIQUE (email)
);

CREATE TABLE profiles (
    id BIGSERIAL PRIMARY KEY,
    bio TEXT,
    avatar_url VARCHAR(512),
    headline VARCHAR(255),
    social_links JSONB,
    preferences JSONB,
    user_id BIGINT NOT NULL,
    created_at TIMESTAMPTZ NOT NULL DEFAULT CURRENT_TIMESTAMP,
    updated_at TIMESTAMPTZ NOT NULL DEFAULT CURRENT_TIMESTAMP,
    CONSTRAINT uk_profile_user UNIQUE (user_id),
    CONSTRAINT fk_profile_user FOREIGN KEY (user_id) REFERENCES users (id) ON DELETE CASCADE
);

CREATE TABLE categories (
    id BIGSERIAL PRIMARY KEY,
    name VARCHAR(120) NOT NULL,
    slug VARCHAR(120) NOT NULL,
    description VARCHAR(1000),
    icon VARCHAR(120),
    created_at TIMESTAMPTZ NOT NULL DEFAULT CURRENT_TIMESTAMP,
    updated_at TIMESTAMPTZ NOT NULL DEFAULT CURRENT_TIMESTAMP,
    CONSTRAINT uk_category_name UNIQUE (name),
    CONSTRAINT uk_category_slug UNIQUE (slug)
);

CREATE TABLE tags (
    id BIGSERIAL PRIMARY KEY,
    name VARCHAR(120) NOT NULL,
    slug VARCHAR(120) NOT NULL,
    description VARCHAR(500),
    created_at TIMESTAMPTZ NOT NULL DEFAULT CURRENT_TIMESTAMP,
    updated_at TIMESTAMPTZ NOT NULL DEFAULT CURRENT_TIMESTAMP,
    CONSTRAINT uk_tag_name UNIQUE (name),
    CONSTRAINT uk_tag_slug UNIQUE (slug)
);

CREATE TABLE courses (
    id BIGSERIAL PRIMARY KEY,
    title VARCHAR(255) NOT NULL,
    slug VARCHAR(255) NOT NULL,
    summary VARCHAR(500),
    description TEXT,
    difficulty_level VARCHAR(20) NOT NULL DEFAULT 'BEGINNER',
    estimated_duration VARCHAR(120),
    language VARCHAR(50),
    published_at TIMESTAMPTZ,
    status VARCHAR(20) NOT NULL DEFAULT 'DRAFT',
    category_id BIGINT,
    teacher_id BIGINT NOT NULL,
    created_at TIMESTAMPTZ NOT NULL DEFAULT CURRENT_TIMESTAMP,
    updated_at TIMESTAMPTZ NOT NULL DEFAULT CURRENT_TIMESTAMP,
    CONSTRAINT fk_course_category FOREIGN KEY (category_id) REFERENCES categories (id) ON DELETE SET NULL,
    CONSTRAINT fk_course_teacher FOREIGN KEY (teacher_id) REFERENCES users (id) ON DELETE RESTRICT,
    CONSTRAINT uk_course_teacher_slug UNIQUE (teacher_id, slug)
);

CREATE INDEX idx_course_teacher_status ON courses (teacher_id, status);
CREATE INDEX idx_course_category ON courses (category_id);

CREATE TABLE modules (
    id BIGSERIAL PRIMARY KEY,
    title VARCHAR(255) NOT NULL,
    description VARCHAR(1000),
    order_index INTEGER NOT NULL,
    unlock_date TIMESTAMPTZ,
    is_mandatory BOOLEAN NOT NULL DEFAULT TRUE,
    course_id BIGINT NOT NULL,
    created_at TIMESTAMPTZ NOT NULL DEFAULT CURRENT_TIMESTAMP,
    updated_at TIMESTAMPTZ NOT NULL DEFAULT CURRENT_TIMESTAMP,
    CONSTRAINT fk_module_course FOREIGN KEY (course_id) REFERENCES courses (id) ON DELETE CASCADE
);

CREATE INDEX idx_module_course ON modules (course_id);

CREATE TABLE lessons (
    id BIGSERIAL PRIMARY KEY,
    title VARCHAR(255) NOT NULL,
    summary VARCHAR(500),
    content TEXT,
    video_url VARCHAR(512),
    duration_minutes INTEGER,
    order_index INTEGER NOT NULL,
    module_id BIGINT NOT NULL,
    created_at TIMESTAMPTZ NOT NULL DEFAULT CURRENT_TIMESTAMP,
    updated_at TIMESTAMPTZ NOT NULL DEFAULT CURRENT_TIMESTAMP,
    CONSTRAINT fk_lesson_module FOREIGN KEY (module_id) REFERENCES modules (id) ON DELETE CASCADE
);

CREATE INDEX idx_lesson_module ON lessons (module_id);

CREATE TABLE lesson_resources (
    id BIGSERIAL PRIMARY KEY,
    title VARCHAR(255) NOT NULL,
    resource_type VARCHAR(20) NOT NULL,
    url VARCHAR(512),
    file_path VARCHAR(512),
    order_index INTEGER NOT NULL,
    lesson_id BIGINT NOT NULL,
    metadata JSONB,
    created_at TIMESTAMPTZ NOT NULL DEFAULT CURRENT_TIMESTAMP,
    updated_at TIMESTAMPTZ NOT NULL DEFAULT CURRENT_TIMESTAMP,
    CONSTRAINT fk_resource_lesson FOREIGN KEY (lesson_id) REFERENCES lessons (id) ON DELETE CASCADE
);

CREATE INDEX idx_resource_lesson ON lesson_resources (lesson_id);

CREATE TABLE assignments (
    id BIGSERIAL PRIMARY KEY,
    title VARCHAR(255) NOT NULL,
    description TEXT,
    due_at TIMESTAMPTZ,
    max_score INTEGER,
    submission_type VARCHAR(20) NOT NULL DEFAULT 'TEXT',
    grading_type VARCHAR(20) NOT NULL DEFAULT 'NUMERIC',
    status VARCHAR(20) NOT NULL DEFAULT 'DRAFT',
    order_index INTEGER NOT NULL,
    release_at TIMESTAMPTZ,
    lesson_id BIGINT NOT NULL,
    creator_id BIGINT,
    allow_late_submission BOOLEAN NOT NULL DEFAULT FALSE,
    late_penalty_percentage INTEGER,
    lock_after_deadline BOOLEAN NOT NULL DEFAULT FALSE,
    discussion_enabled BOOLEAN NOT NULL DEFAULT TRUE,
    grading_deadline TIMESTAMPTZ,
    created_at TIMESTAMPTZ NOT NULL DEFAULT CURRENT_TIMESTAMP,
    updated_at TIMESTAMPTZ NOT NULL DEFAULT CURRENT_TIMESTAMP,
    CONSTRAINT fk_assignment_lesson FOREIGN KEY (lesson_id) REFERENCES lessons (id) ON DELETE CASCADE,
    CONSTRAINT fk_assignment_creator FOREIGN KEY (creator_id) REFERENCES users (id) ON DELETE SET NULL
);

CREATE INDEX idx_assignment_lesson ON assignments (lesson_id);

CREATE TABLE submissions (
    id BIGSERIAL PRIMARY KEY,
    assignment_id BIGINT NOT NULL,
    student_id BIGINT NOT NULL,
    grader_id BIGINT,
    status VARCHAR(25) NOT NULL DEFAULT 'DRAFT',
    submitted_at TIMESTAMPTZ,
    content TEXT,
    attachment_path VARCHAR(512),
    score INTEGER,
    feedback TEXT,
    graded_at TIMESTAMPTZ,
    graded_by BIGINT,
    feedback_updated_at TIMESTAMPTZ,
    created_at TIMESTAMPTZ NOT NULL DEFAULT CURRENT_TIMESTAMP,
    updated_at TIMESTAMPTZ NOT NULL DEFAULT CURRENT_TIMESTAMP,
    CONSTRAINT fk_submission_assignment FOREIGN KEY (assignment_id) REFERENCES assignments (id) ON DELETE CASCADE,
    CONSTRAINT fk_submission_student FOREIGN KEY (student_id) REFERENCES users (id) ON DELETE CASCADE,
    CONSTRAINT fk_submission_grader FOREIGN KEY (grader_id) REFERENCES users (id) ON DELETE SET NULL,
    CONSTRAINT uk_submission_assignment_student UNIQUE (assignment_id, student_id)
);

CREATE INDEX idx_submission_assignment ON submissions (assignment_id);
CREATE INDEX idx_submission_student ON submissions (student_id);

CREATE TABLE enrollments (
    id BIGSERIAL PRIMARY KEY,
    enrolled_at TIMESTAMPTZ NOT NULL,
    status VARCHAR(20) NOT NULL DEFAULT 'PENDING',
    progress_percentage NUMERIC(5, 2) DEFAULT 0,
    last_access_at TIMESTAMPTZ,
    student_id BIGINT NOT NULL,
    course_id BIGINT NOT NULL,
    created_at TIMESTAMPTZ NOT NULL DEFAULT CURRENT_TIMESTAMP,
    updated_at TIMESTAMPTZ NOT NULL DEFAULT CURRENT_TIMESTAMP,
    CONSTRAINT fk_enrollment_student FOREIGN KEY (student_id) REFERENCES users (id) ON DELETE CASCADE,
    CONSTRAINT fk_enrollment_course FOREIGN KEY (course_id) REFERENCES courses (id) ON DELETE CASCADE,
    CONSTRAINT uk_enrollment_course_student UNIQUE (course_id, student_id)
);

CREATE INDEX idx_enrollment_course ON enrollments (course_id);
CREATE INDEX idx_enrollment_student ON enrollments (student_id);

CREATE TABLE course_reviews (
    id BIGSERIAL PRIMARY KEY,
    rating INTEGER NOT NULL,
    comment TEXT,
    is_public BOOLEAN NOT NULL DEFAULT TRUE,
    course_id BIGINT NOT NULL,
    student_id BIGINT NOT NULL,
    enrollment_id BIGINT,
    created_at TIMESTAMPTZ NOT NULL DEFAULT CURRENT_TIMESTAMP,
    updated_at TIMESTAMPTZ NOT NULL DEFAULT CURRENT_TIMESTAMP,
    CONSTRAINT fk_review_course FOREIGN KEY (course_id) REFERENCES courses (id) ON DELETE CASCADE,
    CONSTRAINT fk_review_student FOREIGN KEY (student_id) REFERENCES users (id) ON DELETE CASCADE,
    CONSTRAINT fk_review_enrollment FOREIGN KEY (enrollment_id) REFERENCES enrollments (id) ON DELETE SET NULL,
    CONSTRAINT uk_course_review_student UNIQUE (course_id, student_id)
);

CREATE TABLE announcements (
    id BIGSERIAL PRIMARY KEY,
    title VARCHAR(255) NOT NULL,
    content TEXT NOT NULL,
    published_at TIMESTAMPTZ NOT NULL,
    visible_until TIMESTAMPTZ,
    course_id BIGINT NOT NULL,
    author_id BIGINT NOT NULL,
    created_at TIMESTAMPTZ NOT NULL DEFAULT CURRENT_TIMESTAMP,
    updated_at TIMESTAMPTZ NOT NULL DEFAULT CURRENT_TIMESTAMP,
    CONSTRAINT fk_announcement_course FOREIGN KEY (course_id) REFERENCES courses (id) ON DELETE CASCADE,
    CONSTRAINT fk_announcement_author FOREIGN KEY (author_id) REFERENCES users (id) ON DELETE CASCADE
);

CREATE INDEX idx_announcement_course ON announcements (course_id);

CREATE TABLE course_tags (
    id BIGSERIAL PRIMARY KEY,
    course_id BIGINT NOT NULL,
    tag_id BIGINT NOT NULL,
    created_by BIGINT,
    weight INTEGER,
    created_at TIMESTAMPTZ NOT NULL DEFAULT CURRENT_TIMESTAMP,
    updated_at TIMESTAMPTZ NOT NULL DEFAULT CURRENT_TIMESTAMP,
    CONSTRAINT fk_course_tag_course FOREIGN KEY (course_id) REFERENCES courses (id) ON DELETE CASCADE,
    CONSTRAINT fk_course_tag_tag FOREIGN KEY (tag_id) REFERENCES tags (id) ON DELETE CASCADE,
    CONSTRAINT fk_course_tag_creator FOREIGN KEY (created_by) REFERENCES users (id) ON DELETE SET NULL,
    CONSTRAINT uk_course_tag_pair UNIQUE (course_id, tag_id)
);

CREATE INDEX idx_course_tag_course ON course_tags (course_id);
CREATE INDEX idx_course_tag_tag ON course_tags (tag_id);

CREATE TABLE quizzes (
    id BIGSERIAL PRIMARY KEY,
    title VARCHAR(255) NOT NULL,
    description TEXT,
    total_points INTEGER,
    time_limit_seconds INTEGER,
    attempt_limit INTEGER,
    grading_method VARCHAR(20) NOT NULL DEFAULT 'HIGHEST',
    quiz_type VARCHAR(20) NOT NULL DEFAULT 'MODULE_FINAL',
    module_id BIGINT,
    course_id BIGINT,
    created_at TIMESTAMPTZ NOT NULL DEFAULT CURRENT_TIMESTAMP,
    updated_at TIMESTAMPTZ NOT NULL DEFAULT CURRENT_TIMESTAMP,
    CONSTRAINT fk_quiz_module FOREIGN KEY (module_id) REFERENCES modules (id) ON DELETE CASCADE,
    CONSTRAINT fk_quiz_course FOREIGN KEY (course_id) REFERENCES courses (id) ON DELETE SET NULL,
    CONSTRAINT uk_quiz_module UNIQUE (module_id)
);

CREATE INDEX idx_quiz_course ON quizzes (course_id);

CREATE TABLE quiz_questions (
    id BIGSERIAL PRIMARY KEY,
    text TEXT NOT NULL,
    explanation TEXT,
    points INTEGER,
    question_type VARCHAR(20) NOT NULL,
    order_index INTEGER NOT NULL,
    quiz_id BIGINT NOT NULL,
    created_at TIMESTAMPTZ NOT NULL DEFAULT CURRENT_TIMESTAMP,
    updated_at TIMESTAMPTZ NOT NULL DEFAULT CURRENT_TIMESTAMP,
    CONSTRAINT fk_question_quiz FOREIGN KEY (quiz_id) REFERENCES quizzes (id) ON DELETE CASCADE
);

CREATE INDEX idx_question_quiz ON quiz_questions (quiz_id);

CREATE TABLE answer_options (
    id BIGSERIAL PRIMARY KEY,
    text TEXT NOT NULL,
    is_correct BOOLEAN NOT NULL,
    feedback TEXT,
    order_index INTEGER NOT NULL,
    question_id BIGINT NOT NULL,
    created_at TIMESTAMPTZ NOT NULL DEFAULT CURRENT_TIMESTAMP,
    updated_at TIMESTAMPTZ NOT NULL DEFAULT CURRENT_TIMESTAMP,
    CONSTRAINT fk_answer_question FOREIGN KEY (question_id) REFERENCES quiz_questions (id) ON DELETE CASCADE
);

CREATE INDEX idx_answer_option_question ON answer_options (question_id);

CREATE TABLE quiz_submissions (
    id BIGSERIAL PRIMARY KEY,
    quiz_id BIGINT NOT NULL,
    student_id BIGINT NOT NULL,
    enrollment_id BIGINT,
    status VARCHAR(20) NOT NULL DEFAULT 'IN_PROGRESS',
    started_at TIMESTAMPTZ,
    completed_at TIMESTAMPTZ,
    score INTEGER,
    attempt_number INTEGER NOT NULL DEFAULT 1,
    created_at TIMESTAMPTZ NOT NULL DEFAULT CURRENT_TIMESTAMP,
    updated_at TIMESTAMPTZ NOT NULL DEFAULT CURRENT_TIMESTAMP,
    CONSTRAINT fk_quiz_submission_quiz FOREIGN KEY (quiz_id) REFERENCES quizzes (id) ON DELETE CASCADE,
    CONSTRAINT fk_quiz_submission_student FOREIGN KEY (student_id) REFERENCES users (id) ON DELETE CASCADE,
    CONSTRAINT fk_quiz_submission_enrollment FOREIGN KEY (enrollment_id) REFERENCES enrollments (id) ON DELETE SET NULL,
    CONSTRAINT uk_quiz_submission_attempt UNIQUE (quiz_id, student_id, attempt_number)
);

CREATE INDEX idx_quiz_submission_quiz ON quiz_submissions (quiz_id);
CREATE INDEX idx_quiz_submission_student ON quiz_submissions (student_id);

CREATE TABLE quiz_submission_answers (
    id BIGSERIAL PRIMARY KEY,
    quiz_submission_id BIGINT NOT NULL,
    question_id BIGINT NOT NULL,
    answer_option_id BIGINT,
    answer_text TEXT,
    is_correct BOOLEAN,
    awarded_points INTEGER,
    created_at TIMESTAMPTZ NOT NULL DEFAULT CURRENT_TIMESTAMP,
    updated_at TIMESTAMPTZ NOT NULL DEFAULT CURRENT_TIMESTAMP,
    CONSTRAINT fk_submission_answer_submission FOREIGN KEY (quiz_submission_id) REFERENCES quiz_submissions (id) ON DELETE CASCADE,
    CONSTRAINT fk_submission_answer_question FOREIGN KEY (question_id) REFERENCES quiz_questions (id) ON DELETE CASCADE,
    CONSTRAINT fk_submission_answer_option FOREIGN KEY (answer_option_id) REFERENCES answer_options (id) ON DELETE SET NULL,
    CONSTRAINT uk_submission_answer_unique UNIQUE (quiz_submission_id, question_id, answer_option_id)
);

CREATE INDEX idx_submission_answer_submission ON quiz_submission_answers (quiz_submission_id);
CREATE INDEX idx_submission_answer_question ON quiz_submission_answers (question_id);


