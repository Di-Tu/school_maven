ALTER TABLE student ADD CONSTRAINT min_age_limit CHECK (age > 15);
ALTER TABLE student ADD CONSTRAINT name_unique UNIQUE (name);
ALTER TABLE student ADD CONSTRAINT name_not_empty CHECK (name IS NOT NULL AND TRIM(name) <> '');
ALTER TABLE faculty ADD CONSTRAINT login_pass_unique UNIQUE (name, color);
ALTER TABLE student ALTER COLUMN age SET DEFAULT 20;