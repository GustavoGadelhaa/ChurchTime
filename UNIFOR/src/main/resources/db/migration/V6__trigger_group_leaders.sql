-- Trigger para sincronizar group_leaders quando líder é atribuído/removido na tabela groups

-- Função que insere/remover líder na tabela group_leaders
CREATE OR REPLACE FUNCTION sync_group_leader()
RETURNS TRIGGER AS $$
BEGIN
    -- Se líder foi removido (set null)
    IF OLD.leader_id IS NOT NULL AND NEW.leader_id IS NULL THEN
        DELETE FROM group_leaders 
        WHERE user_id = OLD.leader_id AND group_id = NEW.id;
        RETURN NEW;
    END IF;

    -- Se líder foi alterado (remover anterior)
    IF OLD.leader_id IS NOT NULL AND NEW.leader_id IS NOT NULL AND OLD.leader_id != NEW.leader_id THEN
        DELETE FROM group_leaders 
        WHERE user_id = OLD.leader_id AND group_id = NEW.id;
    END IF;

    -- Se novo líder foi atribuído
    IF NEW.leader_id IS NOT NULL THEN
        INSERT INTO group_leaders (church_id, user_id, group_id, created_at, updated_at)
        VALUES (NEW.church_id, NEW.leader_id, NEW.id, NOW(), NOW())
        ON CONFLICT (user_id, group_id) DO NOTHING;
    END IF;

    RETURN NEW;
END;
$$ LANGUAGE plpgsql;

-- Trigger para AFTER INSERT/UPDATE na tabela groups
CREATE TRIGGER trg_group_leader_sync
    AFTER INSERT OR UPDATE OF leader_id
    ON groups
    FOR EACH ROW
    EXECUTE FUNCTION sync_group_leader();

-- =============================================================================
-- Trigger na tabela users: role = LEADER requer group_id associado
-- =============================================================================
-- Função que valida e sincroniza role LEADER do usuário
CREATE OR REPLACE FUNCTION sync_user_to_leader()
RETURNS TRIGGER AS $$
DECLARE
    v_church_id BIGINT;
BEGIN
    -- Se role mudou para LEADER
    IF OLD.role != 'LEADER' AND NEW.role = 'LEADER' THEN
        -- Verifica se usuário tem group_id associado
        IF NEW.group_id IS NULL THEN
            RAISE EXCEPTION 'Para definir como líder, o usuário precisa ter um grupo associado (group_id)';
        END IF;

        -- Busca church_id do grupo
        SELECT church_id INTO v_church_id FROM groups WHERE id = NEW.group_id;

        -- Insere na tabela group_leaders
        INSERT INTO group_leaders (church_id, user_id, group_id, created_at, updated_at)
        VALUES (v_church_id, NEW.id, NEW.group_id, NOW(), NOW())
        ON CONFLICT (user_id, group_id) DO NOTHING;
    END IF;

    -- Se role mudou de LEADER para outro
    IF OLD.role = 'LEADER' AND NEW.role != 'LEADER' THEN
        DELETE FROM group_leaders WHERE user_id = NEW.id;
    END IF;

    RETURN NEW;
END;
$$ LANGUAGE plpgsql;

-- Trigger para AFTER INSERT/UPDATE na tabela users
CREATE TRIGGER trg_user_leader_sync
    AFTER INSERT OR UPDATE OF role, group_id
    ON users
    FOR EACH ROW
    EXECUTE FUNCTION sync_user_to_leader();