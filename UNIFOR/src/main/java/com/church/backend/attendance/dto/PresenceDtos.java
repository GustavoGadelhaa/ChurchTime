package com.church.backend.attendance.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.RequiredArgsConstructor;

import java.time.Instant;

public final class PresenceDtos {

	private PresenceDtos() {
	}

	@Data
	@AllArgsConstructor
	public static class PresenceResponse {
		private Long id;
		private Long userId;
		private String userName;
		private Instant checkedInAt;
	}
}
