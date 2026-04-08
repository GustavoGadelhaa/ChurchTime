package com.church.backend.identity.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.RequiredArgsConstructor;

import java.time.Instant;

public final class ChurchDtos {

	private ChurchDtos() {
	}

	@Data
	@AllArgsConstructor
	public static class ChurchResponse {
		private Long id;
		private String name;
		private boolean active;
		private Instant createdAt;
	}

	@Data
	@AllArgsConstructor
	public static class CreateChurchRequest {
		@NotBlank
		@Size(max = 150)
		private String name;
	}

	@Data
	@AllArgsConstructor
	public static class UpdateChurchRequest {
		@NotBlank
		@Size(max = 150)
		private String name;
	}
}
