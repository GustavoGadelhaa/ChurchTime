-- Dados mock para desenvolvimento (requer V2__seed_dev aplicado).
-- Senha de todos os usuários abaixo: admin123 (mesmo hash BCrypt do seed).
-- Admin: admin@church.local | Líder grupo 1: carlos.lider@church.local | Membros: ana@church.local, bruno@church.local, carla@church.local

INSERT INTO users (id, group_id, name, email, password_hash, phone, role, active, created_at, updated_at)
VALUES (2, NULL, 'Carlos Líder', 'carlos.lider@church.local',
        '$2b$12$.akLgJPOcPmQW6lUpatycuWsIB4g5wnGnH7yvq/6AC072rK3Bw6UW',
        '85991111111', 'LEADER', TRUE, NOW(), NOW());

UPDATE groups SET leader_id = 2 WHERE id = 1;

INSERT INTO users (id, group_id, name, email, password_hash, phone, role, active, created_at, updated_at)
VALUES
    (3, 1, 'Ana Membro', 'ana@church.local',
     '$2b$12$.akLgJPOcPmQW6lUpatycuWsIB4g5wnGnH7yvq/6AC072rK3Bw6UW',
     '85992222222', 'MEMBER', TRUE, NOW(), NOW()),
    (4, 1, 'Bruno Membro', 'bruno@church.local',
     '$2b$12$.akLgJPOcPmQW6lUpatycuWsIB4g5wnGnH7yvq/6AC072rK3Bw6UW',
     NULL, 'MEMBER', TRUE, NOW(), NOW()),
    (5, 1, 'Carla Membro', 'carla@church.local',
     '$2b$12$.akLgJPOcPmQW6lUpatycuWsIB4g5wnGnH7yvq/6AC072rK3Bw6UW',
     NULL, 'MEMBER', TRUE, NOW(), NOW());

UPDATE users SET group_id = 1 WHERE id = 2;

INSERT INTO churches (id, name, active, created_at)
VALUES (2, 'Comunidade Boa Vista', TRUE, NOW());

INSERT INTO groups (id, church_id, leader_id, name, description, active, created_at)
VALUES (2, 2, NULL, 'Jovens BV', 'Encontro semanal — sábado', TRUE, NOW());

INSERT INTO users (id, group_id, name, email, password_hash, phone, role, active, created_at, updated_at)
VALUES (6, NULL, 'Diana Líder', 'diana@church.local',
        '$2b$12$.akLgJPOcPmQW6lUpatycuWsIB4g5wnGnH7yvq/6AC072rK3Bw6UW',
        NULL, 'LEADER', TRUE, NOW(), NOW());

UPDATE groups SET leader_id = 6 WHERE id = 2;

INSERT INTO users (id, group_id, name, email, password_hash, phone, role, active, created_at, updated_at)
VALUES (7, 2, 'Eduardo Membro', 'edu@church.local',
        '$2b$12$.akLgJPOcPmQW6lUpatycuWsIB4g5wnGnH7yvq/6AC072rK3Bw6UW',
        NULL, 'MEMBER', TRUE, NOW(), NOW());

UPDATE users SET group_id = 2 WHERE id = 6;

INSERT INTO events (id, group_id, title, location, event_date, status, created_at)
VALUES
    (1, 1, 'Culto de domingo', 'Sede principal', (NOW() + INTERVAL '7 days'), 'SCHEDULED', NOW()),
    (2, 1, 'Reunião de célula', 'Casa do líder', (NOW() - INTERVAL '1 day'), 'CLOSED', NOW()),
    (3, 1, 'Encontro mensal', 'Templo', NOW(), 'OPEN', NOW()),
    (4, 2, 'Escola jovem', 'Salão social', (NOW() + INTERVAL '3 days'), 'SCHEDULED', NOW());

INSERT INTO presences (id, event_id, user_id, checked_in_at)
VALUES
    (1, 2, 3, (NOW() - INTERVAL '1 day' + INTERVAL '10 minutes')),
    (2, 2, 4, (NOW() - INTERVAL '1 day' + INTERVAL '18 minutes')),
    (3, 3, 3, (NOW() - INTERVAL '30 minutes'));

SELECT setval(pg_get_serial_sequence('churches', 'id'), (SELECT COALESCE(MAX(id), 1) FROM churches));
SELECT setval(pg_get_serial_sequence('groups', 'id'), (SELECT COALESCE(MAX(id), 1) FROM groups));
SELECT setval(pg_get_serial_sequence('users', 'id'), (SELECT COALESCE(MAX(id), 1) FROM users));
SELECT setval(pg_get_serial_sequence('events', 'id'), (SELECT COALESCE(MAX(id), 1) FROM events));
SELECT setval(pg_get_serial_sequence('presences', 'id'), (SELECT COALESCE(MAX(id), 1) FROM presences));
