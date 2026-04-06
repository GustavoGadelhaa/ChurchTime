package com.church.backend.identity.service;

import com.church.backend.identity.dto.GroupDtos.AssignLeaderRequest;
import com.church.backend.identity.dto.GroupDtos.CreateGroupRequest;
import com.church.backend.identity.dto.GroupDtos.GroupResponse;
import com.church.backend.identity.dto.GroupDtos.UpdateGroupRequest;
import com.church.backend.identity.entity.Group;
import com.church.backend.identity.repository.GroupRepository;
import com.church.backend.identity.repository.UserRepository;
import com.church.backend.shared.exception.BadRequestException;
import com.church.backend.shared.exception.NotFoundException;
import com.church.backend.identity.entity.UserRole;
import com.church.backend.shared.exception.ForbiddenException;
import com.church.backend.shared.security.AccessPolicy;
import com.church.backend.shared.security.CurrentUserService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
@RequiredArgsConstructor
@Transactional
public class GroupService {

	private final GroupRepository groupRepository;
	private final UserRepository userRepository;
	private final ChurchService churchService;
	private final CurrentUserService currentUserService;
	private final AccessPolicy accessPolicy;

	@Transactional(readOnly = true)
	public List<GroupResponse> listByChurch(Long churchId) {
		var current = currentUserService.requireCurrent();
		accessPolicy.requireChurchScopedForGroups(churchId, current);
		churchService.requireActiveChurch(churchId);
		return groupRepository.findByChurchIdAndActiveTrueOrderByNameAsc(churchId).stream()
				.map(GroupService::toResponse)
				.toList();
	}

	@Transactional(readOnly = true)
	public List<GroupResponse> listMyChurchGroups() {
		var current = currentUserService.requireCurrent();
		if (current.getGroup() == null) {
			return List.of();
		}
		Long churchId = current.getGroup().getChurch().getId();
		return groupRepository.findByChurchIdAndActiveTrueOrderByNameAsc(churchId).stream()
				.map(GroupService::toResponse)
				.toList();
	}

	public GroupResponse joinGroup(Long groupId) {
		var current = currentUserService.requireCurrent();
		if (current.getRole() != UserRole.MEMBER) {
			throw new ForbiddenException("Apenas membros podem trocar de grupo");
		}
		Group target = requireActiveGroup(groupId);
		if (current.getGroup() == null) {
			throw new BadRequestException("Usuário sem grupo associado");
		}
		Long currentChurchId = current.getGroup().getChurch().getId();
		if (!target.getChurch().getId().equals(currentChurchId)) {
			throw new ForbiddenException("Grupo pertence a outra igreja");
		}
		current.setGroup(target);
		userRepository.save(current);
		return toResponse(target);
	}

	@Transactional(readOnly = true)
	public GroupResponse get(Long id) {
		var current = currentUserService.requireCurrent();
		accessPolicy.requireGroupRead(id, current);
		Group group = requireActiveGroup(id);
		return toResponse(group);
	}

	public GroupResponse create(Long churchId, CreateGroupRequest request) {
		accessPolicy.requireAdmin(currentUserService.requireCurrent());
		var church = churchService.requireActiveChurch(churchId);
		Group group = Group.builder()
				.church(church)
				.name(request.name().trim())
				.description(request.description() != null ? request.description().trim() : null)
				.build();
		return toResponse(groupRepository.save(group));
	}

	public GroupResponse update(Long id, UpdateGroupRequest request) {
		accessPolicy.requireAdmin(currentUserService.requireCurrent());
		Group group = requireActiveGroup(id);
		group.setName(request.name().trim());
		group.setDescription(request.description() != null ? request.description().trim() : null);
		return toResponse(groupRepository.save(group));
	}

	public GroupResponse assignLeader(Long id, AssignLeaderRequest request) {
		accessPolicy.requireAdmin(currentUserService.requireCurrent());
		Group group = requireActiveGroup(id);
		if (request.leaderUserId() == null) {
			group.setLeader(null);
			return toResponse(groupRepository.save(group));
		}
		var leader = userRepository.findById(request.leaderUserId())
				.orElseThrow(() -> new NotFoundException("Líder não encontrado"));
		if (!leader.isActive()) {
			throw new BadRequestException("Usuário inativo");
		}
		List<Group> leaderGroups = groupRepository.findByLeaderIdAndActiveTrue(leader.getId());
		boolean alreadyLeadsAnotherGroup = leaderGroups.stream()
				.anyMatch(g -> !g.getId().equals(group.getId()));
		if (alreadyLeadsAnotherGroup) {
			throw new BadRequestException("Este usuário já é líder de outro grupo");
		}
		group.setLeader(leader);
		return toResponse(groupRepository.save(group));
	}

	public void delete(Long id) {
		accessPolicy.requireAdmin(currentUserService.requireCurrent());
		Group group = groupRepository.findById(id).orElseThrow(() -> new NotFoundException("Grupo não encontrado"));
		if (!group.isActive()) {
			return;
		}
		group.setActive(false);
	}

	public Group requireActiveGroup(Long id) {
		Group group = groupRepository.findById(id).orElseThrow(() -> new NotFoundException("Grupo não encontrado"));
		if (!group.isActive()) {
			throw new NotFoundException("Grupo não encontrado");
		}
		return group;
	}

	private static GroupResponse toResponse(Group group) {
		Long leaderId = group.getLeader() != null ? group.getLeader().getId() : null;
		return new GroupResponse(
				group.getId(),
				group.getChurch().getId(),
				leaderId,
				group.getName(),
				group.getDescription(),
				group.isActive(),
				group.getCreatedAt()
		);
	}
}
