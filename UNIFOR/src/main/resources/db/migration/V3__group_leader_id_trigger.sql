-- V3: Trigger para atualizar leader_id na tabela groups quando role do user mudar

-- 1. Função do trigger para atualizar group.leader_id
CREATE OR REPLACE FUNCTION update_group_leader_id_on_role_change()
RETURNS TRIGGER AS $$
BEGIN
    -- Se role mudou para LEADER e tem group_id, atualizar leader_id em groups
    IF NEW.role = 'LEADER' AND OLD.role != 'LEADER' AND NEW.group_id IS NOT NULL THEN
        UPDATE groups 
        SET leader_id = NEW.id 
        WHERE id = NEW.group_id;
    END IF;

    -- Se role deixou de ser LEADER, remover leader_id de groups
    IF OLD.role = 'LEADER' AND NEW.role != 'LEADER' AND OLD.group_id IS NOT NULL THEN
        UPDATE groups 
        SET leader_id = NULL 
        WHERE id = OLD.group_id AND leader_id = OLD.id;
    END IF;

    -- Se mudou de grupo sendo LEADER
    IF OLD.role = 'LEADER' AND (NEW.group_id IS DISTINCT FROM OLD.group_id) THEN
        UPDATE groups 
        SET leader_id = NULL 
        WHERE id = OLD.group_id AND leader_id = OLD.id;
        
        IF NEW.group_id IS NOT NULL THEN
            UPDATE groups 
            SET leader_id = NEW.id 
            WHERE id = NEW.group_id;
        END IF;
    END IF;

    RETURN NEW;
END;
$$ LANGUAGE plpgsql;

-- 2. Criar o trigger
DROP TRIGGER IF EXISTS trg_update_group_leader_id ON users;

CREATE TRIGGER trg_update_group_leader_id
    AFTER UPDATE ON users
    FOR EACH ROW
    EXECUTE FUNCTION update_group_leader_id_on_role_change();