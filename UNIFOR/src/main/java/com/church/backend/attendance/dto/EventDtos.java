package com.church.backend.attendance.dto;

import com.church.backend.attendance.entity.EventStatus;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.RequiredArgsConstructor;

import java.time.Instant;

public final class EventDtos {

	private EventDtos() {
	}

	@Data
	@AllArgsConstructor
	public static class EventResponse {
		private Long id;
		private Long groupId;
		private String title;
		private String location;
		private Instant eventDate;
		private EventStatus status;
		private Instant createdAt;
	}

	@Data
	@AllArgsConstructor
	public static class CreateEventRequest {
		@NotBlank
		@Size(max = 150)
		private String title;

		@Size(max = 255)
		private String location;

		@NotNull
		private Instant eventDate;

		private EventStatus status;
	}

	@Data
	@AllArgsConstructor
	public static class UpdateEventRequest {
		@NotBlank
		@Size(max = 150)
		private String title;

		@Size(max = 255)
		private String location;

		@NotNull
		private Instant eventDate;

		@NotNull
		private EventStatus status;
	}
}
