-- Default admin: admin@church.local / admin123 (change in production)
INSERT INTO churches (id, name, active, created_at)
VALUES (1, 'Igreja Demo', TRUE, NOW());

INSERT INTO groups (id, church_id, leader_id, name, description, active, created_at)
VALUES (1, 1, NULL, 'Grupo Demo', 'Grupo para testes', TRUE, NOW());

INSERT INTO users (id, group_id, name, email, password_hash, phone, role, active, created_at, updated_at)
VALUES (
    1,
    NULL,
    'Administrador',
    'admin@church.local',
    '$2b$12$.akLgJPOcPmQW6lUpatycuWsIB4g5wnGnH7yvq/6AC072rK3Bw6UW',
    NULL,
    'ADMIN',
    TRUE,
    NOW(),
    NOW()
);

SELECT setval(pg_get_serial_sequence('churches', 'id'), (SELECT MAX(id) FROM churches));
SELECT setval(pg_get_serial_sequence('groups', 'id'), (SELECT MAX(id) FROM groups));
SELECT setval(pg_get_serial_sequence('users', 'id'), (SELECT MAX(id) FROM users));
