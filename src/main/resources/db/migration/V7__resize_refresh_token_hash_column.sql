-- Resize the refresh_token_hash column to TEXT.
--
-- Previously VARCHAR(512) was used to hold raw JWT strings (~400-600 chars).
-- Now that refresh tokens are stored as SHA-256 hashes (44 chars), the column
-- can be resized. TEXT is used for maximum flexibility and simplicity.
--
-- IMPORTANT: Run this only after clearing existing plain-text JWT rows:
--   DELETE FROM user_sessions;
-- (Safe in dev — session tokens are invalidated on restart anyway.)

ALTER TABLE user_sessions ALTER COLUMN refresh_token_hash TYPE TEXT;
