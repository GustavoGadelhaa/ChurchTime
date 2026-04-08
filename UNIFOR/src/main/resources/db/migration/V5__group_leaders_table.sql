CREATE TABLE group_leaders (
    id BIGSERIAL PRIMARY KEY,
    church_id BIGINT NOT NULL REFERENCES churches (id),
    user_id BIGINT NOT NULL REFERENCES users (id),
    group_id BIGINT NOT NULL REFERENCES groups (id),
    created_at TIMESTAMP NOT NULL,
    updated_at TIMESTAMP NOT NULL,
    CONSTRAINT uk_group_leaders_user_group UNIQUE (user_id, group_id)
);

CREATE INDEX idx_group_leaders_church ON group_leaders (church_id);
CREATE INDEX idx_group_leaders_user ON group_leaders (user_id);
CREATE INDEX idx_group_leaders_group ON group_leaders (group_id);