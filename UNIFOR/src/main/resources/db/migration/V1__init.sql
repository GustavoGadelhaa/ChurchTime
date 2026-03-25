CREATE TABLE churches (
    id BIGSERIAL PRIMARY KEY,
    name VARCHAR(150) NOT NULL,
    active BOOLEAN NOT NULL DEFAULT TRUE,
    created_at TIMESTAMP NOT NULL
);

CREATE TABLE groups (
    id BIGSERIAL PRIMARY KEY,
    church_id BIGINT NOT NULL REFERENCES churches (id),
    leader_id BIGINT,
    name VARCHAR(100) NOT NULL,
    description VARCHAR(255),
    active BOOLEAN NOT NULL DEFAULT TRUE,
    created_at TIMESTAMP NOT NULL
);

CREATE TABLE users (
    id BIGSERIAL PRIMARY KEY,
    group_id BIGINT REFERENCES groups (id),
    name VARCHAR(100) NOT NULL,
    email VARCHAR(150) NOT NULL UNIQUE,
    password_hash VARCHAR(255) NOT NULL,
    phone VARCHAR(20),
    role VARCHAR(20) NOT NULL DEFAULT 'MEMBER',
    active BOOLEAN NOT NULL DEFAULT TRUE,
    created_at TIMESTAMP NOT NULL,
    updated_at TIMESTAMP NOT NULL
);

ALTER TABLE groups
    ADD CONSTRAINT fk_groups_leader FOREIGN KEY (leader_id) REFERENCES users (id);

CREATE TABLE events (
    id BIGSERIAL PRIMARY KEY,
    group_id BIGINT NOT NULL REFERENCES groups (id),
    title VARCHAR(150) NOT NULL,
    location VARCHAR(255),
    event_date TIMESTAMP NOT NULL,
    status VARCHAR(20) NOT NULL DEFAULT 'SCHEDULED',
    created_at TIMESTAMP NOT NULL,
    reminded BOOLEAN default FALSE
);

CREATE TABLE presences (
    id BIGSERIAL PRIMARY KEY,
    event_id BIGINT NOT NULL REFERENCES events (id) ON DELETE CASCADE,
    user_id BIGINT NOT NULL REFERENCES users (id),
    checked_in_at TIMESTAMP NOT NULL,
    CONSTRAINT uk_presences_user_event UNIQUE (user_id, event_id)
);

CREATE INDEX idx_users_group ON users (group_id);
CREATE INDEX idx_groups_church ON groups (church_id);
CREATE INDEX idx_events_group ON events (group_id);
CREATE INDEX idx_presences_event ON presences (event_id);
