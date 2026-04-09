-- Core Performance Indexes (Relationships)
CREATE INDEX idx_csections_course_id ON course_sections(course_id);
CREATE INDEX idx_sitems_section_id ON section_items(section_id);
CREATE INDEX idx_enrollments_user_id ON enrollments(user_id);
CREATE INDEX idx_enrollments_course_id ON enrollments(course_id);
CREATE INDEX idx_sprogress_enrollment_id ON student_progress(enrollment_id);
CREATE INDEX idx_sprogress_item_id ON student_progress(item_id);

-- RBAC Join Table Lookups
CREATE INDEX idx_user_roles_role_id ON user_roles(role_id);
CREATE INDEX idx_role_permissions_permission_id ON role_permissions(permission_id);
CREATE INDEX idx_user_permissions_permission_id ON user_permissions(permission_id);

-- Curriculum & Search Optimization
CREATE INDEX idx_courses_author_id ON courses(author_id);
CREATE INDEX idx_courses_search ON courses USING GIN(search_vector);

-- Quiz & Item Lookups
CREATE INDEX idx_quiz_questions_quiz_id ON quiz_questions(quiz_id);
CREATE INDEX idx_question_options_question_id ON question_options(question_id);
CREATE INDEX idx_sqa_progress_id ON student_quiz_attempts(progress_id);
CREATE INDEX idx_sqa_quiz_id ON student_quiz_attempts(quiz_id);

-- Content Type Mapping (1:1 Relationships)
CREATE INDEX idx_video_item_id ON video_contents(item_id);
CREATE INDEX idx_resource_item_id ON resource_contents(item_id);
CREATE INDEX idx_quizzes_item_id ON quizzes(item_id);
CREATE INDEX idx_coding_task_item_id ON coding_tasks(item_id);

-- Review & Audit Trails
CREATE INDEX idx_review_logs_course_id ON review_logs(course_id);
CREATE INDEX idx_item_revisions_course_id ON item_revisions(course_id);

-- Full-Text Search Trigger Logic for Courses
DROP TRIGGER IF EXISTS tsvectorupdate ON courses;
DROP FUNCTION IF EXISTS courses_search_vector_trigger();
DROP INDEX IF EXISTS idx_courses_search_vector;

CREATE OR REPLACE FUNCTION update_search_vector()
RETURNS trigger AS $$
BEGIN
  NEW.search_vector :=
    to_tsvector('english', coalesce(NEW.title, '') || ' ' || coalesce(NEW.description, ''));
  RETURN NEW;
END;
$$ LANGUAGE plpgsql;

CREATE TRIGGER courses_search_trigger
BEFORE INSERT OR UPDATE
ON courses
FOR EACH ROW
EXECUTE FUNCTION update_search_vector();
