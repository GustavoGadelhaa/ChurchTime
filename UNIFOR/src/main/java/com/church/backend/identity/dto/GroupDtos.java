package com.church.backend.identity.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.RequiredArgsConstructor;

import java.time.Instant;

public final class GroupDtos {

	private GroupDtos() {
	}

	@Data
	@AllArgsConstructor
	public static class GroupResponse {
		private Long id;
		private Long churchId;
		private Long leaderId;
		private String name;
		private String description;
		private boolean active;
		private Instant createdAt;
	}

	@Data
	@AllArgsConstructor
	public static class CreateGroupRequest {
		@NotBlank
		@Size(max = 100)
		private String name;

		@Size(max = 255)
		private String description;
	}

	@Data
	@AllArgsConstructor
	public static class UpdateGroupRequest {
		@NotBlank
		@Size(max = 100)
		private String name;

		@Size(max = 255)
		private String description;
	}

	@Data
	@AllArgsConstructor
	public static class AssignLeaderRequest {
		private Long leaderUserId;
	}

	@Data
	@AllArgsConstructor
	public static class MyGroupResponse {
		private Long id;
		private String name;
		private String description;
		private String leaderName;
		private long memberCount;
		private long activeEvents;
		private boolean active;
	}
}
