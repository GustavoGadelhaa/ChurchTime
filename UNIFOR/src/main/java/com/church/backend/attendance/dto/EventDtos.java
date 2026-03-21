package com.church.backend.attendance.dto;

import com.church.backend.attendance.entity.EventStatus;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;

import java.time.Instant;

public final class EventDtos {

	private EventDtos() {
	}

	public record EventResponse(
			Long id,
			Long groupId,
			String title,
			String location,
			Instant eventDate,
			EventStatus status,
			Instant createdAt
	) {
	}

	public record CreateEventRequest(
			@NotBlank @Size(max = 150) String title,
			@Size(max = 255) String location,
			@NotNull Instant eventDate,
			EventStatus status
	) {
	}

	public record UpdateEventRequest(
			@NotBlank @Size(max = 150) String title,
			@Size(max = 255) String location,
			@NotNull Instant eventDate,
			@NotNull EventStatus status
	) {
	}
}
