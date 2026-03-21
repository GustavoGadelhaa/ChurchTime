package com.church.backend.attendance.dto;

import java.time.Instant;

public final class PresenceDtos {

	private PresenceDtos() {
	}

	public record PresenceResponse(Long id, Long userId, String userName, Instant checkedInAt) {
	}
}
