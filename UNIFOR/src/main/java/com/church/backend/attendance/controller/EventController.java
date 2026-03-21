package com.church.backend.attendance.controller;

import com.church.backend.attendance.dto.EventDtos.CreateEventRequest;
import com.church.backend.attendance.dto.EventDtos.EventResponse;
import com.church.backend.attendance.dto.EventDtos.UpdateEventRequest;
import com.church.backend.attendance.service.EventService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequiredArgsConstructor
public class EventController {

	private final EventService eventService;

	@PostMapping("/api/groups/{groupId}/events")
	@ResponseStatus(HttpStatus.CREATED)
	public EventResponse create(@PathVariable Long groupId, @RequestBody @Valid CreateEventRequest request) {
		return eventService.create(groupId, request);
	}

	@GetMapping("/api/groups/{groupId}/events")
	public List<EventResponse> listByGroup(@PathVariable Long groupId) {
		return eventService.listByGroup(groupId);
	}

	@GetMapping("/api/events/{id}")
	public EventResponse get(@PathVariable Long id) {
		return eventService.get(id);
	}

	@PutMapping("/api/events/{id}")
	public EventResponse update(@PathVariable Long id, @RequestBody @Valid UpdateEventRequest request) {
		return eventService.update(id, request);
	}

	@DeleteMapping("/api/events/{id}")
	@ResponseStatus(HttpStatus.NO_CONTENT)
	public void delete(@PathVariable Long id) {
		eventService.delete(id);
	}
}
