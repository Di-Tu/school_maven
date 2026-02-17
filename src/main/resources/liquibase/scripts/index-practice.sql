-- liquibase formatted sql
-- changeset dkochetov:1
CREATE INDEX idx_student_name ON student (name);

-- changeset dkochetov:2
CREATE INDEX idx_faculty_name_color ON faculty (name, color);
