package com.church.backend.attendance.controller;

import com.church.backend.attendance.dto.PresenceDtos.PresenceResponse;
import com.church.backend.attendance.service.PresenceService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequiredArgsConstructor
@Slf4j
public class PresenceController {

	private final PresenceService presenceService;

	@PostMapping("/api/events/{eventId}/checkin")
	@ResponseStatus(HttpStatus.CREATED)
	public PresenceResponse checkIn(@PathVariable Long eventId) {
		log.info("[PRESENCE] POST /api/events/{}/checkin - Timestamp: {}", 
				eventId, java.time.LocalDateTime.now());
		PresenceResponse response = presenceService.checkIn(eventId);
		log.info("[PRESENCE] POST /api/events/{}/checkin - SUCCESS - UserId: {}, Timestamp: {}", 
				eventId, response.getUserId(), java.time.LocalDateTime.now());
		return response;
	}

	@GetMapping("/api/events/{eventId}/presences")
	public List<PresenceResponse> list(@PathVariable Long eventId) {
		return presenceService.listByEvent(eventId);
	}
}
