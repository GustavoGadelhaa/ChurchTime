-- V2: Constraint e Trigger para role LEADER usando group_leaders

-- 0. Adicionar unique constraint em group_id da tabela group_leaders (se não existir)
ALTER TABLE group_leaders ADD CONSTRAINT uk_group_leaders_group UNIQUE (group_id);

-- 1. Constraint: só pode setar role = 'LEADER' se tiver group_id (se não existir)
DO $$
BEGIN
    IF NOT EXISTS (SELECT 1 FROM pg_constraint WHERE conname = 'chk_leader_has_group') THEN
        ALTER TABLE users ADD CONSTRAINT chk_leader_has_group
        CHECK ((role != 'LEADER') OR (role = 'LEADER' AND group_id IS NOT NULL));
    END IF;
END $$;

-- 2. Função do trigger para atualizar group_leaders
CREATE OR REPLACE FUNCTION update_group_leader_on_role_change()
RETURNS TRIGGER AS $$
BEGIN
    -- Se role mudou para LEADER e tem group_id, inserir/atualizar em group_leaders
    IF NEW.role = 'LEADER' AND OLD.role != 'LEADER' AND NEW.group_id IS NOT NULL THEN
        INSERT INTO group_leaders (church_id, user_id, group_id)
        VALUES (NEW.group_id, NEW.id, NEW.group_id)
        ON CONFLICT (group_id) DO UPDATE SET user_id = NEW.id;
    END IF;

    -- Se role deixou de ser LEADER, remover de group_leaders
    IF OLD.role = 'LEADER' AND NEW.role != 'LEADER' AND OLD.group_id IS NOT NULL THEN
        DELETE FROM group_leaders WHERE group_id = OLD.group_id;
    END IF;

    -- Se mudou de grupo sendo LEADER
    IF OLD.role = 'LEADER' AND NEW.group_id != OLD.group_id THEN
        DELETE FROM group_leaders WHERE group_id = OLD.group_id;
        IF NEW.group_id IS NOT NULL THEN
            INSERT INTO group_leaders (church_id, user_id, group_id)
            VALUES (NEW.group_id, NEW.id, NEW.group_id)
            ON CONFLICT (group_id) DO UPDATE SET user_id = NEW.id;
        END IF;
    END IF;

    RETURN NEW;
END;
$$ LANGUAGE plpgsql;

-- 3. Criar o trigger (recriar para garantir)
DROP TRIGGER IF EXISTS trg_update_group_leader ON users;

CREATE TRIGGER trg_update_group_leader
    AFTER UPDATE ON users
    FOR EACH ROW
    EXECUTE FUNCTION update_group_leader_on_role_change();