package com.church.backend.identity.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;

import java.time.Instant;

public final class ChurchDtos {

	private ChurchDtos() {
	}

	public record ChurchResponse(Long id, String name, boolean active, Instant createdAt) {
	}

	public record CreateChurchRequest(
			@NotBlank @Size(max = 150) String name
	) {
	}

	public record UpdateChurchRequest(
			@NotBlank @Size(max = 150) String name
	) {
	}
}
