package com.church.backend.attendance.controller;

import com.church.backend.attendance.dto.PresenceDtos.PresenceResponse;
import com.church.backend.attendance.service.PresenceService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequiredArgsConstructor
public class PresenceController {

	private final PresenceService presenceService;

	@PostMapping("/api/events/{eventId}/checkin")
	@ResponseStatus(HttpStatus.CREATED)
	public PresenceResponse checkIn(@PathVariable Long eventId) {
		return presenceService.checkIn(eventId);
	}

	@GetMapping("/api/events/{eventId}/presences")
	public List<PresenceResponse> list(@PathVariable Long eventId) {
		return presenceService.listByEvent(eventId);
	}
}
