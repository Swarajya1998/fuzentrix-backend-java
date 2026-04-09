-- Enforce 1:1 relationship between Section Items and their specific Content
-- This ensures that one "Item" in the curriculum sidebar corresponds to exactly one content data row.

ALTER TABLE video_contents ADD CONSTRAINT uq_video_item_id UNIQUE (item_id);
ALTER TABLE resource_contents ADD CONSTRAINT uq_resource_item_id UNIQUE (item_id);
ALTER TABLE coding_tasks ADD CONSTRAINT uq_coding_task_item_id UNIQUE (item_id);
ALTER TABLE quizzes ADD CONSTRAINT uq_quiz_item_id UNIQUE (item_id);
