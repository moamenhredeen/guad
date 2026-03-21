-- Sequences
CREATE SEQUENCE IF NOT EXISTS actions_seq START WITH 1 INCREMENT BY 50;
CREATE SEQUENCE IF NOT EXISTS areas_seq START WITH 1 INCREMENT BY 50;
CREATE SEQUENCE IF NOT EXISTS attachments_seq START WITH 1 INCREMENT BY 50;
CREATE SEQUENCE IF NOT EXISTS contexts_seq START WITH 1 INCREMENT BY 50;
CREATE SEQUENCE IF NOT EXISTS documents_seq START WITH 1 INCREMENT BY 50;
CREATE SEQUENCE IF NOT EXISTS inbox_items_seq START WITH 1 INCREMENT BY 50;
CREATE SEQUENCE IF NOT EXISTS projects_seq START WITH 1 INCREMENT BY 50;
CREATE SEQUENCE IF NOT EXISTS waiting_for_items_seq START WITH 1 INCREMENT BY 50;
CREATE SEQUENCE IF NOT EXISTS weekly_reviews_seq START WITH 1 INCREMENT BY 50;
CREATE SEQUENCE IF NOT EXISTS user_profiles_seq START WITH 1 INCREMENT BY 50;

-- Tables
CREATE TABLE actions
(
    id                 BIGINT                      NOT NULL,
    description        VARCHAR(255),
    notes              VARCHAR(255),
    status             SMALLINT,
    is_time_specific   BOOLEAN,
    estimated_duration INTEGER,
    energy_level       INTEGER,
    location           VARCHAR(255),
    created_date       TIMESTAMP WITHOUT TIME ZONE NOT NULL,
    updated_date       TIMESTAMP WITHOUT TIME ZONE,
    completed_date     TIMESTAMP WITHOUT TIME ZONE,
    scheduled_date     TIMESTAMP WITHOUT TIME ZONE,
    due_date           TIMESTAMP WITHOUT TIME ZONE,
    project_id         BIGINT,
    area_id            BIGINT,
    user_id            UUID                        NOT NULL,
    CONSTRAINT pk_actions PRIMARY KEY (id)
);

CREATE TABLE areas
(
    id          BIGINT       NOT NULL,
    name        VARCHAR(255) NOT NULL,
    description VARCHAR(255),
    "order"     INTEGER,
    user_id     UUID         NOT NULL,
    CONSTRAINT pk_areas PRIMARY KEY (id)
);

CREATE TABLE attachments
(
    id            BIGINT NOT NULL,
    filename      VARCHAR(255),
    file_size     BIGINT,
    mime_type     VARCHAR(255),
    file_url      VARCHAR(255),
    uploaded_date TIMESTAMP WITHOUT TIME ZONE,
    user_id       UUID   NOT NULL,
    CONSTRAINT pk_attachments PRIMARY KEY (id)
);

CREATE TABLE contexts
(
    id          BIGINT       NOT NULL,
    name        VARCHAR(255) NOT NULL,
    description VARCHAR(255),
    color       VARCHAR(255),
    icon_key    VARCHAR(255),
    user_id     UUID,
    CONSTRAINT pk_contexts PRIMARY KEY (id)
);

CREATE TABLE documents
(
    id         BIGINT       NOT NULL,
    name       VARCHAR(255) NOT NULL,
    content    VARCHAR(255),
    project_id BIGINT,
    CONSTRAINT pk_documents PRIMARY KEY (id)
);

CREATE TABLE inbox_items
(
    id             BIGINT       NOT NULL,
    title          VARCHAR(255) NOT NULL,
    description    VARCHAR(255),
    status         SMALLINT     NOT NULL,
    created_date   TIMESTAMP WITHOUT TIME ZONE,
    updated_date   TIMESTAMP WITHOUT TIME ZONE,
    processed_date TIMESTAMP WITHOUT TIME ZONE,
    user_id        UUID         NOT NULL,
    CONSTRAINT pk_inbox_items PRIMARY KEY (id)
);

CREATE TABLE projects
(
    id              BIGINT NOT NULL,
    name            VARCHAR(255),
    description     VARCHAR(255),
    desired_outcome VARCHAR(255),
    status          SMALLINT,
    created_date    TIMESTAMP WITHOUT TIME ZONE,
    updated_date    TIMESTAMP WITHOUT TIME ZONE,
    completed_date  TIMESTAMP WITHOUT TIME ZONE,
    color           VARCHAR(255),
    user_id         UUID   NOT NULL,
    area_id         BIGINT,
    CONSTRAINT pk_projects PRIMARY KEY (id)
);

CREATE TABLE waiting_for_items
(
    id             BIGINT                      NOT NULL,
    title          VARCHAR(255)                NOT NULL,
    delegated_to   VARCHAR(255),
    delegated_at   TIMESTAMP WITHOUT TIME ZONE,
    follow_up_date TIMESTAMP WITHOUT TIME ZONE,
    notes          VARCHAR(255),
    status         SMALLINT                    NOT NULL DEFAULT 0,
    action_id      BIGINT,
    project_id     BIGINT,
    user_id        UUID                        NOT NULL,
    created_date   TIMESTAMP WITHOUT TIME ZONE NOT NULL,
    updated_date   TIMESTAMP WITHOUT TIME ZONE,
    completed_date TIMESTAMP WITHOUT TIME ZONE,
    CONSTRAINT pk_waiting_for_items PRIMARY KEY (id)
);

CREATE TABLE weekly_reviews
(
    id           BIGINT                      NOT NULL,
    started_at   TIMESTAMP WITHOUT TIME ZONE NOT NULL,
    completed_at TIMESTAMP WITHOUT TIME ZONE,
    current_step SMALLINT                    NOT NULL DEFAULT 0,
    notes        VARCHAR(255),
    user_id      UUID                        NOT NULL,
    CONSTRAINT pk_weekly_reviews PRIMARY KEY (id)
);

CREATE TABLE user_profiles
(
    id                             BIGINT       NOT NULL DEFAULT nextval('user_profiles_seq'),
    keycloak_id                    UUID         NOT NULL,
    email                          VARCHAR(255) NOT NULL,
    display_name                   VARCHAR(255) NOT NULL,
    timezone                       VARCHAR(50)  NOT NULL DEFAULT 'Europe/Berlin',
    default_review_day             VARCHAR(10)  NOT NULL DEFAULT 'SATURDAY',
    energy_tracking_enabled        BOOLEAN      NOT NULL DEFAULT TRUE,
    email_digests_enabled          BOOLEAN      NOT NULL DEFAULT FALSE,
    reminder_notifications_enabled BOOLEAN      NOT NULL DEFAULT TRUE,
    created_date                   TIMESTAMPTZ  NOT NULL DEFAULT now(),
    updated_date                   TIMESTAMPTZ  NOT NULL DEFAULT now(),
    CONSTRAINT pk_user_profiles PRIMARY KEY (id),
    CONSTRAINT uq_user_profiles_keycloak_id UNIQUE (keycloak_id)
);

-- Join tables
CREATE TABLE action_attachments
(
    action        BIGINT NOT NULL,
    attachment_id BIGINT NOT NULL,
    CONSTRAINT pk_action_attachments PRIMARY KEY (action, attachment_id)
);

CREATE TABLE action_contexts
(
    action_id  BIGINT NOT NULL,
    context_id BIGINT NOT NULL,
    CONSTRAINT pk_action_contexts PRIMARY KEY (action_id, context_id)
);

CREATE TABLE document_attachments
(
    attachment_id BIGINT NOT NULL,
    document_id   BIGINT NOT NULL,
    CONSTRAINT pk_document_attachments PRIMARY KEY (attachment_id, document_id)
);

CREATE TABLE inbox_item_attachments
(
    attachment_id BIGINT NOT NULL,
    inbox_item_id BIGINT NOT NULL,
    CONSTRAINT pk_inbox_item_attachments PRIMARY KEY (attachment_id, inbox_item_id)
);

CREATE TABLE project_attachments
(
    attachment_id BIGINT NOT NULL,
    project_id    BIGINT NOT NULL,
    CONSTRAINT pk_project_attachments PRIMARY KEY (attachment_id, project_id)
);

-- Foreign key constraints
ALTER TABLE actions
    ADD CONSTRAINT FK_ACTIONS_ON_AREA FOREIGN KEY (area_id) REFERENCES areas (id);

ALTER TABLE actions
    ADD CONSTRAINT FK_ACTIONS_ON_PROJECT FOREIGN KEY (project_id) REFERENCES projects (id);

ALTER TABLE documents
    ADD CONSTRAINT FK_DOCUMENTS_ON_PROJECT FOREIGN KEY (project_id) REFERENCES projects (id);

ALTER TABLE projects
    ADD CONSTRAINT FK_PROJECTS_ON_AREA FOREIGN KEY (area_id) REFERENCES areas (id);

ALTER TABLE waiting_for_items
    ADD CONSTRAINT fk_wfi_action FOREIGN KEY (action_id) REFERENCES actions (id);

ALTER TABLE waiting_for_items
    ADD CONSTRAINT fk_wfi_project FOREIGN KEY (project_id) REFERENCES projects (id);

ALTER TABLE action_attachments
    ADD CONSTRAINT fk_actatt_on_action FOREIGN KEY (action) REFERENCES actions (id);

ALTER TABLE action_attachments
    ADD CONSTRAINT fk_actatt_on_attachment FOREIGN KEY (attachment_id) REFERENCES attachments (id);

ALTER TABLE action_contexts
    ADD CONSTRAINT fk_actcon_on_action FOREIGN KEY (action_id) REFERENCES actions (id);

ALTER TABLE action_contexts
    ADD CONSTRAINT fk_actcon_on_context FOREIGN KEY (context_id) REFERENCES contexts (id);

ALTER TABLE document_attachments
    ADD CONSTRAINT fk_docatt_on_attachment FOREIGN KEY (attachment_id) REFERENCES attachments (id);

ALTER TABLE document_attachments
    ADD CONSTRAINT fk_docatt_on_document FOREIGN KEY (document_id) REFERENCES documents (id);

ALTER TABLE inbox_item_attachments
    ADD CONSTRAINT fk_inbiteatt_on_attachment FOREIGN KEY (attachment_id) REFERENCES attachments (id);

ALTER TABLE inbox_item_attachments
    ADD CONSTRAINT fk_inbiteatt_on_inbox_item FOREIGN KEY (inbox_item_id) REFERENCES inbox_items (id);

ALTER TABLE project_attachments
    ADD CONSTRAINT fk_proatt_on_attachment FOREIGN KEY (attachment_id) REFERENCES attachments (id);

ALTER TABLE project_attachments
    ADD CONSTRAINT fk_proatt_on_project FOREIGN KEY (project_id) REFERENCES projects (id);

-- Indexes
CREATE INDEX idx_actions_user_id ON actions (user_id);
CREATE INDEX idx_areas_user_id ON areas (user_id);
CREATE INDEX idx_contexts_user_id ON contexts (user_id);
CREATE INDEX idx_inbox_items_user_id ON inbox_items (user_id);
CREATE INDEX idx_projects_user_id ON projects (user_id);
CREATE INDEX idx_waiting_for_items_user_id ON waiting_for_items (user_id);
CREATE INDEX idx_weekly_reviews_user_id ON weekly_reviews (user_id);
