-- Drop FK constraints referencing users table (user_id is now a standalone UUID, not a FK)
ALTER TABLE actions DROP CONSTRAINT IF EXISTS FK_ACTIONS_ON_USER;
ALTER TABLE areas DROP CONSTRAINT IF EXISTS FK_AREAS_ON_USER;
ALTER TABLE attachments DROP CONSTRAINT IF EXISTS FK_ATTACHMENTS_ON_USER;
ALTER TABLE contexts DROP CONSTRAINT IF EXISTS FK_CONTEXTS_ON_USER;
ALTER TABLE inbox_items DROP CONSTRAINT IF EXISTS FK_INBOX_ITEMS_ON_USER;
ALTER TABLE projects DROP CONSTRAINT IF EXISTS FK_PROJECTS_ON_USER;

-- Convert user_id from BIGINT to UUID (no production data, safe to alter)
ALTER TABLE actions ALTER COLUMN user_id TYPE UUID USING gen_random_uuid();
ALTER TABLE areas ALTER COLUMN user_id TYPE UUID USING gen_random_uuid();
ALTER TABLE attachments ALTER COLUMN user_id TYPE UUID USING gen_random_uuid();
ALTER TABLE contexts ALTER COLUMN user_id TYPE UUID USING gen_random_uuid();
ALTER TABLE inbox_items ALTER COLUMN user_id TYPE UUID USING gen_random_uuid();
ALTER TABLE projects ALTER COLUMN user_id TYPE UUID USING gen_random_uuid();

-- Add indexes on user_id
CREATE INDEX idx_actions_user_id ON actions (user_id);
CREATE INDEX idx_areas_user_id ON areas (user_id);
CREATE INDEX idx_contexts_user_id ON contexts (user_id);
CREATE INDEX idx_inbox_items_user_id ON inbox_items (user_id);
CREATE INDEX idx_projects_user_id ON projects (user_id);

-- Waiting For Items
CREATE SEQUENCE IF NOT EXISTS waiting_for_items_seq START WITH 1 INCREMENT BY 50;
CREATE TABLE waiting_for_items (
    id              BIGINT                      NOT NULL,
    title           VARCHAR(255)                NOT NULL,
    delegated_to    VARCHAR(255),
    delegated_at    TIMESTAMP WITHOUT TIME ZONE,
    follow_up_date  TIMESTAMP WITHOUT TIME ZONE,
    notes           VARCHAR(255),
    status          SMALLINT                    NOT NULL DEFAULT 0,
    action_id       BIGINT,
    project_id      BIGINT,
    user_id         UUID                        NOT NULL,
    created_date    TIMESTAMP WITHOUT TIME ZONE NOT NULL,
    updated_date    TIMESTAMP WITHOUT TIME ZONE,
    completed_date  TIMESTAMP WITHOUT TIME ZONE,
    CONSTRAINT pk_waiting_for_items PRIMARY KEY (id),
    CONSTRAINT fk_wfi_action FOREIGN KEY (action_id) REFERENCES actions (id),
    CONSTRAINT fk_wfi_project FOREIGN KEY (project_id) REFERENCES projects (id)
);
CREATE INDEX idx_waiting_for_items_user_id ON waiting_for_items (user_id);

-- Weekly Reviews
CREATE SEQUENCE IF NOT EXISTS weekly_reviews_seq START WITH 1 INCREMENT BY 50;
CREATE TABLE weekly_reviews (
    id              BIGINT                      NOT NULL,
    started_at      TIMESTAMP WITHOUT TIME ZONE NOT NULL,
    completed_at    TIMESTAMP WITHOUT TIME ZONE,
    current_step    SMALLINT                    NOT NULL DEFAULT 0,
    notes           VARCHAR(255),
    user_id         UUID                        NOT NULL,
    CONSTRAINT pk_weekly_reviews PRIMARY KEY (id)
);
CREATE INDEX idx_weekly_reviews_user_id ON weekly_reviews (user_id);
