-- Create new Enums
CREATE TYPE resource_type AS ENUM ('PDF', 'LINK', 'DOCUMENT', 'ARCHIVE');
CREATE TYPE question_type AS ENUM ('SINGLE_SELECT', 'MULTI_SELECT', 'TRUE_FALSE', 'SHORT_ANSWER');

-- ==========================================
-- 🔻 CORE USERS & CURRICULUM 🔻
-- ==========================================

CREATE TABLE courses (
    id UUID PRIMARY KEY DEFAULT gen_random_uuid(),
    title VARCHAR(255) NOT NULL,
    description TEXT,
    author_id UUID NOT NULL,
    status publish_status DEFAULT 'DRAFT',
    is_deleted BOOLEAN DEFAULT FALSE,
    thumbnail_img_key VARCHAR(255),
    search_vector tsvector,
    
    created_at TIMESTAMP WITH TIME ZONE DEFAULT CURRENT_TIMESTAMP,
    created_by UUID,
    updated_at TIMESTAMP WITH TIME ZONE DEFAULT CURRENT_TIMESTAMP,
    updated_by UUID,
    
    CONSTRAINT fk_courses_author FOREIGN KEY (author_id) REFERENCES users (id) ON DELETE RESTRICT,
    CONSTRAINT fk_courses_created_by FOREIGN KEY (created_by) REFERENCES users (id) ON DELETE SET NULL,
    CONSTRAINT fk_courses_updated_by FOREIGN KEY (updated_by) REFERENCES users (id) ON DELETE SET NULL
);

-- Index and Trigger for tsvector (Full-Text Search)
CREATE INDEX idx_courses_search_vector ON courses USING gin(search_vector);

CREATE FUNCTION courses_search_vector_trigger() RETURNS trigger AS $$
begin
  new.search_vector :=
     setweight(to_tsvector('english', coalesce(new.title,'')), 'A') ||
     setweight(to_tsvector('english', coalesce(new.description,'')), 'B');
  return new;
end
$$ LANGUAGE plpgsql;

CREATE TRIGGER tsvectorupdate BEFORE INSERT OR UPDATE
    ON courses FOR EACH ROW EXECUTE PROCEDURE courses_search_vector_trigger();

CREATE TABLE course_sections (
    id BIGSERIAL PRIMARY KEY,
    course_id UUID NOT NULL,
    title VARCHAR(255) NOT NULL,
    sort_order INT NOT NULL,
    status publish_status DEFAULT 'DRAFT',
    is_deleted BOOLEAN DEFAULT FALSE,
    
    created_at TIMESTAMP WITH TIME ZONE DEFAULT CURRENT_TIMESTAMP,
    created_by UUID,
    updated_at TIMESTAMP WITH TIME ZONE DEFAULT CURRENT_TIMESTAMP,
    updated_by UUID,
    
    CONSTRAINT fk_csections_course FOREIGN KEY (course_id) REFERENCES courses (id) ON DELETE CASCADE,
    CONSTRAINT fk_csections_created_by FOREIGN KEY (created_by) REFERENCES users (id) ON DELETE SET NULL,
    CONSTRAINT fk_csections_updated_by FOREIGN KEY (updated_by) REFERENCES users (id) ON DELETE SET NULL
);

CREATE TABLE section_items (
    id BIGSERIAL PRIMARY KEY,
    section_id BIGINT NOT NULL,
    title VARCHAR(255) NOT NULL,
    item_type item_type NOT NULL,
    sort_order INT NOT NULL,
    status publish_status DEFAULT 'DRAFT',
    is_deleted BOOLEAN DEFAULT FALSE,
    
    created_at TIMESTAMP WITH TIME ZONE DEFAULT CURRENT_TIMESTAMP,
    created_by UUID,
    updated_at TIMESTAMP WITH TIME ZONE DEFAULT CURRENT_TIMESTAMP,
    updated_by UUID,
    
    CONSTRAINT fk_sitems_section FOREIGN KEY (section_id) REFERENCES course_sections (id) ON DELETE CASCADE,
    CONSTRAINT fk_sitems_created_by FOREIGN KEY (created_by) REFERENCES users (id) ON DELETE SET NULL,
    CONSTRAINT fk_sitems_updated_by FOREIGN KEY (updated_by) REFERENCES users (id) ON DELETE SET NULL
);

-- ==========================================
-- 🔻 ENROLLMENTS (High-Level Access) 🔻
-- ==========================================

CREATE TABLE enrollments (
    id UUID PRIMARY KEY DEFAULT gen_random_uuid(),
    user_id UUID NOT NULL,
    course_id UUID NOT NULL,
    status enrollment_status DEFAULT 'ACTIVE',
    type enrollment_type DEFAULT 'MANUAL_ADMIN',
    
    enrolled_by UUID,
    enrolled_at TIMESTAMP WITH TIME ZONE DEFAULT CURRENT_TIMESTAMP,
    
    completion_percentage INT DEFAULT 0,
    completed_at TIMESTAMP WITH TIME ZONE,
    
    CONSTRAINT fk_enroll_user FOREIGN KEY (user_id) REFERENCES users (id) ON DELETE CASCADE,
    CONSTRAINT fk_enroll_course FOREIGN KEY (course_id) REFERENCES courses (id) ON DELETE CASCADE,
    CONSTRAINT fk_enroll_admin FOREIGN KEY (enrolled_by) REFERENCES users (id) ON DELETE SET NULL,
    CONSTRAINT uq_enrollment_user_course UNIQUE (user_id, course_id)
);

CREATE TABLE student_progress (
    id BIGSERIAL PRIMARY KEY,
    enrollment_id UUID NOT NULL,
    item_id BIGINT NOT NULL,
    status progress_status DEFAULT 'NOT_STARTED',
    
    last_watched_position_seconds INT DEFAULT 0,
    
    started_at TIMESTAMP WITH TIME ZONE,
    completed_at TIMESTAMP WITH TIME ZONE,
    
    CONSTRAINT fk_sprogress_enroll FOREIGN KEY (enrollment_id) REFERENCES enrollments (id) ON DELETE CASCADE,
    CONSTRAINT fk_sprogress_item FOREIGN KEY (item_id) REFERENCES section_items (id) ON DELETE CASCADE,
    CONSTRAINT uq_sprogress_enroll_item UNIQUE (enrollment_id, item_id)
);

-- ==========================================
-- 🔻 CONTENT TYPES 🔻
-- ==========================================

CREATE TABLE video_contents (
    id BIGSERIAL PRIMARY KEY,
    item_id BIGINT NOT NULL,
    s3_bucket VARCHAR(255),
    s3_key VARCHAR(255),
    duration_seconds BIGINT,
    CONSTRAINT fk_video_item FOREIGN KEY (item_id) REFERENCES section_items (id) ON DELETE CASCADE
);

CREATE TABLE resource_contents (
    id BIGSERIAL PRIMARY KEY,
    item_id BIGINT NOT NULL,
    file_url VARCHAR(255),
    file_size_bytes BIGINT,
    resource_type resource_type,
    CONSTRAINT fk_resource_item FOREIGN KEY (item_id) REFERENCES section_items (id) ON DELETE CASCADE
);

CREATE TABLE coding_tasks (
    id BIGSERIAL PRIMARY KEY,
    item_id BIGINT NOT NULL,
    problem_statement TEXT,
    starter_code TEXT,
    expected_output TEXT,
    CONSTRAINT fk_codingt_item FOREIGN KEY (item_id) REFERENCES section_items (id) ON DELETE CASCADE
);

-- ==========================================
-- 🔻 QUIZ ENGINE TABLES 🔻
-- ==========================================

CREATE TABLE quizzes (
    id BIGSERIAL PRIMARY KEY,
    item_id BIGINT NOT NULL,
    passing_score_percentage INT,
    max_attempts INT,
    CONSTRAINT fk_quizzes_item FOREIGN KEY (item_id) REFERENCES section_items (id) ON DELETE CASCADE
);

CREATE TABLE quiz_questions (
    id BIGSERIAL PRIMARY KEY,
    quiz_id BIGINT NOT NULL,
    question_text TEXT NOT NULL,
    attached_image_s3_key VARCHAR(255),
    question_type question_type NOT NULL,
    points INT DEFAULT 1,
    sort_order INT NOT NULL,
    CONSTRAINT fk_qq_quiz FOREIGN KEY (quiz_id) REFERENCES quizzes (id) ON DELETE CASCADE
);

CREATE TABLE question_options (
    id BIGSERIAL PRIMARY KEY,
    question_id BIGINT NOT NULL,
    option_text TEXT NOT NULL,
    attached_image_s3_key VARCHAR(255),
    is_correct BOOLEAN DEFAULT FALSE,
    explanation TEXT,
    CONSTRAINT fk_qo_question FOREIGN KEY (question_id) REFERENCES quiz_questions (id) ON DELETE CASCADE
);

CREATE TABLE student_quiz_attempts (
    id BIGSERIAL PRIMARY KEY,
    progress_id BIGINT NOT NULL,
    quiz_id BIGINT NOT NULL,
    score_percentage INT,
    is_passing BOOLEAN,
    started_at TIMESTAMP WITH TIME ZONE DEFAULT CURRENT_TIMESTAMP,
    submitted_at TIMESTAMP WITH TIME ZONE,
    CONSTRAINT fk_sqa_progress FOREIGN KEY (progress_id) REFERENCES student_progress (id) ON DELETE CASCADE,
    CONSTRAINT fk_sqa_quiz FOREIGN KEY (quiz_id) REFERENCES quizzes (id) ON DELETE CASCADE
);

-- ==========================================
-- 🔻 CONTENT RESTORE & REVIEWS 🔻
-- ==========================================

CREATE TABLE review_logs (
    id BIGSERIAL PRIMARY KEY,
    course_id UUID NOT NULL,
    item_id BIGINT,
    reviewer_id UUID NOT NULL,
    previous_status publish_status,
    new_status publish_status,
    feedback_comments TEXT,
    
    created_at TIMESTAMP WITH TIME ZONE DEFAULT CURRENT_TIMESTAMP,
    
    CONSTRAINT fk_rl_course FOREIGN KEY (course_id) REFERENCES courses (id) ON DELETE CASCADE,
    CONSTRAINT fk_rl_item FOREIGN KEY (item_id) REFERENCES section_items (id) ON DELETE CASCADE,
    CONSTRAINT fk_rl_reviewer FOREIGN KEY (reviewer_id) REFERENCES users (id) ON DELETE SET NULL
);

CREATE TABLE item_revisions (
    id BIGSERIAL PRIMARY KEY,
    course_id UUID NOT NULL,
    item_id BIGINT NOT NULL,
    proposed_payload JSONB,
    status publish_status,
    reviewer_id UUID,
    feedback_comments TEXT,
    
    created_at TIMESTAMP WITH TIME ZONE DEFAULT CURRENT_TIMESTAMP,
    created_by UUID,
    updated_at TIMESTAMP WITH TIME ZONE DEFAULT CURRENT_TIMESTAMP,
    updated_by UUID,
    reviewed_at TIMESTAMP WITH TIME ZONE,
    
    CONSTRAINT fk_ir_course FOREIGN KEY (course_id) REFERENCES courses (id) ON DELETE CASCADE,
    CONSTRAINT fk_ir_item FOREIGN KEY (item_id) REFERENCES section_items (id) ON DELETE CASCADE,
    CONSTRAINT fk_ir_reviewer FOREIGN KEY (reviewer_id) REFERENCES users (id) ON DELETE SET NULL,
    CONSTRAINT fk_ir_created_by FOREIGN KEY (created_by) REFERENCES users (id) ON DELETE SET NULL,
    CONSTRAINT fk_ir_updated_by FOREIGN KEY (updated_by) REFERENCES users (id) ON DELETE SET NULL
);
