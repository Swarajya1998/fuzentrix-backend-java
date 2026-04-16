CREATE TABLE user_profiles (
    user_id UUID PRIMARY KEY,
    full_name VARCHAR(255) NOT NULL,
    avatar_img_key VARCHAR(255),
    headline VARCHAR(255),
    bio TEXT,
    mobile_number VARCHAR(50),
    college_name VARCHAR(255),
    degree VARCHAR(255),
    extended_data JSONB,
    created_at TIMESTAMP WITH TIME ZONE DEFAULT CURRENT_TIMESTAMP,
    updated_at TIMESTAMP WITH TIME ZONE DEFAULT CURRENT_TIMESTAMP,
    CONSTRAINT fk_up_user FOREIGN KEY (user_id) REFERENCES users (id) ON DELETE CASCADE
);
