package com.church.backend.attendance.controller;

import com.church.backend.attendance.dto.EventDtos.CreateEventRequest;
import com.church.backend.attendance.dto.EventDtos.EventResponse;
import com.church.backend.attendance.dto.EventDtos.UpdateEventRequest;
import com.church.backend.attendance.service.EventService;
import com.church.backend.identity.entity.User;
import com.church.backend.identity.service.GroupService;
import com.church.backend.shared.email.EmailService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;

import java.util.List;

@RestController
@RequiredArgsConstructor
@Slf4j
public class EventController {

	private final EventService eventService;
	private final EmailService emailService;
	private final GroupService groupService;

	@PostMapping("/api/groups/{groupId}/events")
	@ResponseStatus(HttpStatus.CREATED)
	public EventResponse create(@PathVariable Long groupId, @RequestBody @Valid CreateEventRequest request) {
		log.info("[EVENT] POST /api/groups/{}/events - Title: {}, Timestamp: {}", groupId, request.getTitle(), java.time.LocalDateTime.now());
		EventResponse response = eventService.create(groupId, request);
		
		List<User> groupList = groupService.listActiveUsersByGroup(groupId);
		String subject = "Novo Evento: " + response.getTitle();
		for (User u : groupList) {
			emailService.sendFromTemplate(u.getEmail(), subject, "new-event-notification", 
				Map.of("userName", u.getName(), "eventTitle", response.getTitle(), 
				       "eventDate", response.getEventDate(), "eventLocation", response.getLocation()));
		}
		
		log.info("[EVENT] POST /api/groups/{}/events - CREATED - EventId: {}, Timestamp: {}", groupId, response.getId(), java.time.LocalDateTime.now());
		return response;
	}

	@GetMapping("/api/groups/{groupId}/events")
	public List<EventResponse> listByGroup(@PathVariable Long groupId) {
		return eventService.listByGroup(groupId);
	}

	@GetMapping("/api/events/{id}")
	public EventResponse get(@PathVariable Long id) {
		return eventService.getForCheckin(id);
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
