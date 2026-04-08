-- ChurchTime - Schema completo + dados iniciais
-- Migration única para criar tudo

-- churches
CREATE TABLE churches (
    id BIGSERIAL PRIMARY KEY,
    name VARCHAR(150) NOT NULL,
    active BOOLEAN NOT NULL DEFAULT TRUE,
    created_at TIMESTAMP NOT NULL
);

-- groups
CREATE TABLE groups (
    id BIGSERIAL PRIMARY KEY,
    church_id BIGINT NOT NULL REFERENCES churches (id),
    leader_id BIGINT,
    name VARCHAR(100) NOT NULL,
    description VARCHAR(255),
    active BOOLEAN NOT NULL DEFAULT TRUE,
    created_at TIMESTAMP NOT NULL
);

-- users
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
    reminded BOOLEAN DEFAULT FALSE
);

CREATE TABLE presences (
    id BIGSERIAL PRIMARY KEY,
    event_id BIGINT NOT NULL REFERENCES events (id) ON DELETE CASCADE,
    user_id BIGINT NOT NULL REFERENCES users (id),
    checked_in_at TIMESTAMP NOT NULL,
    CONSTRAINT uk_presences_user_event UNIQUE (user_id, event_id)
);

CREATE TABLE password_reset_tokens (
    id BIGSERIAL PRIMARY KEY,
    user_id BIGINT NOT NULL REFERENCES users (id),
    token VARCHAR(6) NOT NULL,
    expires_at TIMESTAMP NOT NULL,
    used BOOLEAN DEFAULT FALSE,
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP
);

CREATE TABLE group_leaders (
    id BIGSERIAL PRIMARY KEY,
    church_id BIGINT NOT NULL REFERENCES churches (id),
    user_id BIGINT NOT NULL REFERENCES users (id),
    group_id BIGINT NOT NULL REFERENCES groups (id),
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    updated_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP
);

CREATE INDEX idx_users_group ON users (group_id);
CREATE INDEX idx_groups_church ON groups (church_id);
CREATE INDEX idx_events_group ON events (group_id);
CREATE INDEX idx_presences_event ON presences (event_id);

-- Trigger para updated_at em group_leaders
CREATE OR REPLACE FUNCTION update_updated_at_column()
RETURNS TRIGGER AS $$
BEGIN
    NEW.updated_at = CURRENT_TIMESTAMP;
    RETURN NEW;
END;
$$ LANGUAGE plpgsql;

CREATE TRIGGER update_group_leaders_updated_at
    BEFORE UPDATE ON group_leaders
    FOR EACH ROW
    EXECUTE FUNCTION update_updated_at_column();

-- ==========================================
-- DADOS INICIAIS
-- ==========================================

-- churches
INSERT INTO churches (id, name, active, created_at) VALUES
(1, 'Igreja Demo', TRUE, NOW()),
(2, 'Comunidade Boa Vista', TRUE, NOW());

-- users (sem group_id e leader_id inicialmente)
INSERT INTO users (id, group_id, name, email, password_hash, phone, role, active, created_at, updated_at) VALUES
(1, NULL, 'Administrador', 'admin@church.local', '$2a$12$.akLgJPOcPmQW6lUpatycuWsIB4g5wnGnH7yvq/6AC072rK3Bw6UW', NULL, 'ADMIN', TRUE, NOW(), NOW()),
(2, NULL, 'Carlos Líder', 'carlos.lider@church.local', '$2a$12$.akLgJPOcPmQW6lUpatycuWsIB4g5wnGnH7yvq/6AC072rK3Bw6UW', '85991111111', 'LEADER', TRUE, NOW(), NOW()),
(3, NULL, 'Ana Membro', 'ana@church.local', '$2a$12$.akLgJPOcPmQW6lUpatycuWsIB4g5wnGnH7yvq/6AC072rK3Bw6UW', '85992222222', 'MEMBER', TRUE, NOW(), NOW()),
(4, NULL, 'Bruno Membro', 'bruno@church.local', '$2a$12$.akLgJPOcPmQW6lUpatycuWsIB4g5wnGnH7yvq/6AC072rK3Bw6UW', NULL, 'MEMBER', TRUE, NOW(), NOW()),
(5, NULL, 'Carla Membro', 'carla@church.local', '$2a$12$.akLgJPOcPmQW6lUpatycuWsIB4g5wnGnH7yvq/6AC072rK3Bw6UW', NULL, 'MEMBER', TRUE, NOW(), NOW()),
(6, NULL, 'Diana Líder', 'diana.lider@church.local', '$2a$12$.akLgJPOcPmQW6lUpatycuWsIB4g5wnGnH7yvq/6AC072rK3Bw6UW', NULL, 'LEADER', TRUE, NOW(), NOW()),
(7, NULL, 'Eduardo Membro', 'edu@church.local', '$2a$12$.akLgJPOcPmQW6lUpatycuWsIB4g5wnGnH7yvq/6AC072rK3Bw6UW', NULL, 'MEMBER', TRUE, NOW(), NOW());

-- groups
INSERT INTO groups (id, church_id, leader_id, name, description, active, created_at) VALUES
(1, 1, 2, 'Grupo Demo', 'Grupo para testes', TRUE, NOW()),
(2, 2, 6, 'Jovens BV', 'Encontro semanal', TRUE, NOW());

-- Atualiza users com group_id
UPDATE users SET group_id = 1 WHERE id IN (2, 3, 4, 5);
UPDATE users SET group_id = 2 WHERE id IN (6, 7);

-- events
INSERT INTO events (id, group_id, title, location, event_date, status, created_at, reminded) VALUES
(1, 1, 'Culto de domingo', 'Sede principal', NOW() + INTERVAL '7 days', 'SCHEDULED', NOW(), FALSE),
(2, 1, 'Reunião de célula', 'Casa do líder', NOW() - INTERVAL '1 day', 'CLOSED', NOW(), FALSE),
(3, 1, 'Encontro mensal', 'Templo', NOW(), 'OPEN', NOW(), FALSE),
(4, 2, 'Escola jovem', 'Salão social', NOW() + INTERVAL '3 days', 'SCHEDULED', NOW(), FALSE);

-- presences
INSERT INTO presences (id, event_id, user_id, checked_in_at) VALUES
(1, 2, 3, NOW() - INTERVAL '1 day' + INTERVAL '10 minutes'),
(2, 2, 4, NOW() - INTERVAL '1 day' + INTERVAL '18 minutes'),
(3, 3, 3, NOW() - INTERVAL '30 minutes');

-- group_leaders
INSERT INTO group_leaders (id, church_id, user_id, group_id, created_at, updated_at) VALUES
(1, 1, 2, 1, NOW(), NOW()),
(2, 2, 6, 2, NOW(), NOW());
