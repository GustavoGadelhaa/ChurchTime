package com.church.backend.identity.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;

import java.time.Instant;

public final class GroupDtos {

	private GroupDtos() {
	}

	public record GroupResponse(
			Long id,
			Long churchId,
			Long leaderId,
			String name,
			String description,
			boolean active,
			Instant createdAt
	) {
	}

	public record CreateGroupRequest(
			@NotBlank @Size(max = 100) String name,
			@Size(max = 255) String description
	) {
	}

	public record UpdateGroupRequest(
			@NotBlank @Size(max = 100) String name,
			@Size(max = 255) String description
	) {
	}

	public record AssignLeaderRequest(Long leaderUserId) {
	}

	public record MyGroupResponse(
			Long id,
			String name,
			String description,
			String leaderName,
			long memberCount,
			long activeEvents,
			boolean active
	) {
	}
}
