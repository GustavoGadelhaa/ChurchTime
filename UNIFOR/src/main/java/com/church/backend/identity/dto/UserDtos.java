package com.church.backend.identity.dto;

import com.church.backend.identity.entity.UserRole;
import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;

import java.time.Instant;

public final class UserDtos {

	private UserDtos() {
	}

	public record UserResponse(
			Long id,
			Long groupId,
			String name,
			String email,
			String phone,
			UserRole role,
			boolean active,
			Instant createdAt,
			Instant updatedAt
	) {
	}

	public record CreateUserRequest(
			@NotBlank @Size(max = 100) String name,
			@NotBlank @Email @Size(max = 150) String email,
			@NotBlank @Size(min = 6, max = 100) String password,
			@Size(max = 20) String phone,
			Long groupId
	) {
	}

	public record UpdateUserRequest(
			@NotBlank @Size(max = 100) String name,
			@NotBlank @Email @Size(max = 150) String email,
			@Size(max = 20) String phone,
			@NotNull UserRole role,
			String password
	) {
	}

	public record AssignUserGroupRequest(@NotNull Long groupId) {
	}
}
