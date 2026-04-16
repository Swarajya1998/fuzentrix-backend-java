-- Seed Default Roles into the RBAC System
INSERT INTO roles (name, description) VALUES
('STUDENT', 'Default role for newly registered users, granting basic course access capabilities.'),
('INSTRUCTOR', 'Role for course authors allowing them to create and manage courses.'),
('ADMIN', 'Administrative role capable of managing enrollments and handling platform operations.'),
('SUPER_ADMIN', 'Top level role explicitly required for system-wide technical operations.');
