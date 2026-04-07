package com.church.backend.shared.security;

import com.church.backend.attendance.entity.Event;
import com.church.backend.attendance.entity.EventStatus;
import com.church.backend.identity.entity.Group;
import com.church.backend.identity.entity.User;
import com.church.backend.identity.entity.UserRole;
import com.church.backend.identity.repository.GroupRepository;
import com.church.backend.identity.repository.UserRepository;
import com.church.backend.shared.exception.BadRequestException;
import com.church.backend.shared.exception.ForbiddenException;
import com.church.backend.shared.exception.NotFoundException;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
public class AccessPolicy {

	private final GroupRepository groupRepository;
	private final UserRepository userRepository;

	public void requireAdmin(User current) {
		if (current.getRole() != UserRole.ADMIN) {
			throw new ForbiddenException("Apenas administrador");
		}
	}

	public void requireChurchScopedForGroups(Long churchId, User current) {
		if (current.getRole() == UserRole.ADMIN) {
			return;
		}
		if (current.getRole() == UserRole.LEADER) {
			if (groupRepository.existsByChurchIdAndLeaderIdAndActiveTrue(churchId, current.getId())) {
				return;
			}
			if (current.getGroup() != null
					&& current.getGroup().getChurch() != null
					&& current.getGroup().getChurch().getId().equals(churchId)) {
				return;
			}
			throw new ForbiddenException("Sem acesso a esta igreja");
		}
		// Allow any authenticated user (including MEMBER) to list groups for any church
		return;
	}

	public void requireGroupRead(Long groupId, User current) {
		if (current.getRole() == UserRole.ADMIN) {
			return;
		}
		if (current.getRole() == UserRole.LEADER || current.getRole() == UserRole.MEMBER) {
			Group g = groupRepository.findById(groupId).orElseThrow(() -> new NotFoundException("Grupo não encontrado"));
			if (!g.isActive()) {
				throw new NotFoundException("Grupo não encontrado");
			}
			if (g.getLeader() != null && g.getLeader().getId().equals(current.getId())) {
				return;
			}
			if (current.getGroup() != null && current.getGroup().getId().equals(groupId)) {
				return;
			}
			// MEMBER can read any group in their church
			if (current.getGroup() != null && current.getGroup().getChurch() != null
					&& g.getChurch() != null && g.getChurch().getId().equals(current.getGroup().getChurch().getId())) {
				return;
			}
			throw new ForbiddenException("Sem acesso ao grupo");
		}
		throw new ForbiddenException("Sem permissão");
	}

	public void requireLeaderOrAdminForGroup(Long groupId, User current) {
		if (current.getRole() == UserRole.ADMIN) {
			return;
		}
		if (current.getRole() == UserRole.LEADER) {
			Group g = groupRepository.findById(groupId).orElseThrow(() -> new NotFoundException("Grupo não encontrado"));
			if (g.getLeader() != null && g.getLeader().getId().equals(current.getId())) {
				return;
			}
			throw new ForbiddenException("Apenas líder do grupo ou administrador");
		}
		throw new ForbiddenException("Sem permissão");
	}

	public void requireViewUser(Long targetUserId, User current) {
		if (current.getRole() == UserRole.ADMIN) {
			return;
		}
		if (current.getRole() == UserRole.LEADER) {
			User target = userRepository.findById(targetUserId).orElseThrow(() -> new NotFoundException("Usuário não encontrado"));
			var led = groupRepository.findByLeaderIdAndActiveTrue(current.getId());
			for (Group g : led) {
				if (target.getGroup() != null && target.getGroup().getId().equals(g.getId())) {
					return;
				}
				if (g.getLeader() != null && g.getLeader().getId().equals(target.getId())) {
					return;
				}
			}
			throw new ForbiddenException("Sem permissão para ver este usuário");
		}
		throw new ForbiddenException("Sem permissão");
	}

	public void requireEventAccess(Event event, User current) {
		requireGroupRead(event.getGroup().getId(), current);
	}

	public void requireEventAccessForCheckin(Event event, User current) {
		if (current.getRole() == UserRole.ADMIN) {
			return;
		}
		if (current.getRole() == UserRole.LEADER) {
			Group g = event.getGroup();
			if (g.getLeader() != null && g.getLeader().getId().equals(current.getId())) {
				return;
			}
			if (current.getGroup() != null && current.getGroup().getId().equals(g.getId())) {
				return;
			}
			throw new ForbiddenException("Sem acesso ao evento");
		}
		if (current.getRole() == UserRole.MEMBER) {
			if (current.getGroup() != null && current.getGroup().getId().equals(event.getGroup().getId())) {
				return;
			}
			throw new ForbiddenException("Apenas membros do grupo podem acessar este evento");
		}
		throw new ForbiddenException("Sem permissão");
	}

	public void requirePresencesRead(Event event, User current) {
		if (current.getRole() == UserRole.ADMIN) {
			return;
		}
		if (current.getRole() == UserRole.LEADER) {
			Group g = event.getGroup();
			if (g.getLeader() != null && g.getLeader().getId().equals(current.getId())) {
				return;
			}
			if (current.getGroup() != null && current.getGroup().getId().equals(g.getId())) {
				return;
			}
			throw new ForbiddenException("Sem acesso às presenças");
		}
		if (current.getRole() == UserRole.MEMBER) {
			if (current.getGroup() != null && current.getGroup().getId().equals(event.getGroup().getId())) {
				return;
			}
			throw new ForbiddenException("Apenas membros do grupo podem ver presenças");
		}
		throw new ForbiddenException("Sem permissão");
	}

	public void requireMemberCheckin(Event event, User current) {
		if (current.getRole() != UserRole.MEMBER) {
			throw new ForbiddenException("Apenas membros fazem check-in");
		}
		if (current.getGroup() == null) {
			throw new BadRequestException("Membro sem grupo associado");
		}
		if (!current.getGroup().getId().equals(event.getGroup().getId())) {
			throw new ForbiddenException("Evento de outro grupo");
		}
		if (event.getStatus() != EventStatus.OPEN) {
			throw new BadRequestException("Evento não está aberto para check-in");
		}
	}

	public void requireGroupMembershipManagement(Long groupId, User current) {
		if (current.getRole() == UserRole.ADMIN) {
			return;
		}
		if (current.getRole() == UserRole.LEADER) {
			Group g = groupRepository.findById(groupId).orElseThrow(() -> new NotFoundException("Grupo não encontrado"));
			if (g.getLeader() != null && g.getLeader().getId().equals(current.getId())) {
				return;
			}
			throw new ForbiddenException("Sem permissão para gerenciar membros deste grupo");
		}
		throw new ForbiddenException("Apenas administrador ou líder do grupo podem gerenciar membros");
	}
}
