package com.church.backend.identity.service;

import com.church.backend.identity.dto.UserDtos.AssignUserGroupRequest;
import com.church.backend.identity.dto.UserDtos.CreateUserRequest;
import com.church.backend.identity.dto.UserDtos.UpdateUserRequest;
import com.church.backend.identity.dto.UserDtos.UserResponse;
import com.church.backend.identity.entity.User;
import com.church.backend.identity.entity.UserRole;
import com.church.backend.identity.repository.UserRepository;
import com.church.backend.shared.exception.BadRequestException;
import com.church.backend.shared.exception.NotFoundException;
import com.church.backend.shared.security.AccessPolicy;
import com.church.backend.shared.security.CurrentUserService;
import lombok.RequiredArgsConstructor;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
@RequiredArgsConstructor
@Transactional
public class UserService {

	private final UserRepository userRepository;
	private final GroupService groupService;
	private final PasswordEncoder passwordEncoder;
	private final CurrentUserService currentUserService;
	private final AccessPolicy accessPolicy;

	@Transactional(readOnly = true)
	public List<UserResponse> list() {
		accessPolicy.requireAdmin(currentUserService.requireCurrent());
		return userRepository.findByActiveTrueOrderByNameAsc().stream().map(UserService::toResponse).toList();
	}

	@Transactional(readOnly = true)
	public UserResponse get(Long id) {
		var current = currentUserService.requireCurrent();
		accessPolicy.requireViewUser(id, current);
		User user = requireActiveUser(id);
		return toResponse(user);
	}

	@Transactional(readOnly = true)
	public List<UserResponse> listByGroup(Long groupId) {
		var current = currentUserService.requireCurrent();
		accessPolicy.requireGroupRead(groupId, current);
		groupService.requireActiveGroup(groupId);
		return userRepository.findByGroupIdAndActiveTrueOrderByNameAsc(groupId).stream().map(UserService::toResponse).toList();
	}

	public UserResponse create(CreateUserRequest request) {
		accessPolicy.requireAdmin(currentUserService.requireCurrent());
		String email = request.getEmail().trim().toLowerCase();
		if (userRepository.findByEmailIgnoreCase(email).isPresent()) {
			throw new BadRequestException("E-mail já cadastrado");
		}
		User user = User.builder()
				.name(request.getName().trim())
				.email(email)
				.passwordHash(passwordEncoder.encode(request.getPassword()))
				.phone(trimToNull(request.getPhone()))
				.role(UserRole.MEMBER)
				.build();
		if (request.getGroupId() != null) {
			var group = groupService.requireActiveGroup(request.getGroupId());
			user.setGroup(group);
		}
		return toResponse(userRepository.save(user));
	}

	public UserResponse update(Long id, UpdateUserRequest request) {
		accessPolicy.requireAdmin(currentUserService.requireCurrent());
		User user = requireActiveUser(id);
		String email = request.getEmail().trim().toLowerCase();
		userRepository.findByEmailIgnoreCase(email).ifPresent(other -> {
			if (!other.getId().equals(user.getId())) {
				throw new BadRequestException("E-mail já cadastrado");
			}
		});
		if (request.getRole() == UserRole.ADMIN && user.getRole() != UserRole.ADMIN) {
			throw new BadRequestException("Papel de administrador só pode ser definido diretamente no banco de dados");
		}
		user.setName(request.getName().trim());
		user.setEmail(email);
		user.setPhone(trimToNull(request.getPhone()));
		user.setRole(request.getRole());
		if (request.getPassword() != null && !request.getPassword().isBlank()) {
			if (request.getPassword().length() < 6) {
				throw new BadRequestException("Senha deve ter pelo menos 6 caracteres");
			}
			user.setPasswordHash(passwordEncoder.encode(request.getPassword()));
		}
		return toResponse(userRepository.save(user));
	}

	public UserResponse assignGroup(Long id, AssignUserGroupRequest request) {
		accessPolicy.requireAdmin(currentUserService.requireCurrent());
		User user = requireActiveUser(id);
		var group = groupService.requireActiveGroup(request.getGroupId());
		user.setGroup(group);
		return toResponse(userRepository.save(user));
	}

	public void delete(Long id) {
		accessPolicy.requireAdmin(currentUserService.requireCurrent());
		User user = userRepository.findById(id).orElseThrow(() -> new NotFoundException("Usuário não encontrado"));
		if (!user.isActive()) {
			return;
		}
		user.setActive(false);
	}

	private User requireActiveUser(Long id) {
		User user = userRepository.findById(id).orElseThrow(() -> new NotFoundException("Usuário não encontrado"));
		if (!user.isActive()) {
			throw new NotFoundException("Usuário não encontrado");
		}
		return user;
	}

	private static String trimToNull(String s) {
		if (s == null) {
			return null;
		}
		String t = s.trim();
		return t.isEmpty() ? null : t;
	}

	private static UserResponse toResponse(User user) {
		Long groupId = user.getGroup() != null ? user.getGroup().getId() : null;
		return new UserResponse(
				user.getId(),
				groupId,
				user.getName(),
				user.getEmail(),
				user.getPhone(),
				user.getRole(),
				user.isActive(),
				user.getCreatedAt(),
				user.getUpdatedAt()
		);
	}
}
