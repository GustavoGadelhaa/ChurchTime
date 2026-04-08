package com.church.backend.identity.dto;

import com.church.backend.identity.entity.UserRole;
import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.RequiredArgsConstructor;

import java.time.Instant;

public final class UserDtos {

	private UserDtos() {
	}

	@Data
	@AllArgsConstructor
	public static class UserResponse {
		private Long id;
		private Long groupId;
		private String name;
		private String email;
		private String phone;
		private UserRole role;
		private boolean active;
		private Instant createdAt;
		private Instant updatedAt;
	}

	@Data
	@AllArgsConstructor
	public static class CreateUserRequest {
		@NotBlank
		@Size(max = 100)
		private String name;

		@NotBlank
		@Email
		@Size(max = 150)
		private String email;

		@NotBlank
		@Size(min = 6, max = 100)
		private String password;

		@Size(max = 20)
		private String phone;

		private Long groupId;
	}

	@Data
	@AllArgsConstructor
	public static class UpdateUserRequest {
		@NotBlank
		@Size(max = 100)
		private String name;

		@NotBlank
		@Email
		@Size(max = 150)
		private String email;

		@Size(max = 20)
		private String phone;

		@NotNull
		private UserRole role;

		private String password;
	}

	@Data
	@AllArgsConstructor
	public static class AssignUserGroupRequest {
		@NotNull
		private Long groupId;
	}
}
