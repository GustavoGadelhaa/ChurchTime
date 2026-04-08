package com.church.backend.attendance.service;

import com.church.backend.attendance.dto.EventDtos.CreateEventRequest;
import com.church.backend.attendance.dto.EventDtos.EventResponse;
import com.church.backend.attendance.dto.EventDtos.UpdateEventRequest;
import com.church.backend.attendance.entity.Event;
import com.church.backend.attendance.entity.EventStatus;
import com.church.backend.attendance.repository.EventRepository;
import com.church.backend.identity.entity.UserRole;
import com.church.backend.identity.service.GroupService;
import com.church.backend.shared.exception.NotFoundException;
import com.church.backend.shared.security.AccessPolicy;
import com.church.backend.shared.security.CurrentUserService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Slf4j
@Service
@RequiredArgsConstructor
@Transactional
public class EventService {

	private final EventRepository eventRepository;
	private final GroupService groupService;
	private final CurrentUserService currentUserService;
	private final AccessPolicy accessPolicy;

	@Transactional(readOnly = true)
	public List<EventResponse> listByGroup(Long groupId) {
		var current = currentUserService.requireCurrent();
		accessPolicy.requireGroupRead(groupId, current);
		groupService.requireActiveGroup(groupId);
		return eventRepository.findByGroupIdOrderByEventDateDesc(groupId).stream().map(EventService::toResponse).toList();
	}

	@Transactional(readOnly = true)
	public EventResponse get(Long id) {
		var current = currentUserService.requireCurrent();
		Event event = requireWithGroup(id);
		accessPolicy.requireEventAccess(event, current);
		return toResponse(event);
	}

	@Transactional(readOnly = true)
	public EventResponse getForCheckin(Long id) {
		var current = currentUserService.requireCurrent();
		Event event = requireWithGroup(id);
		// MEMBER can access events for checkin
		if (current.getRole() == UserRole.MEMBER && current.getGroup() != null
				&& current.getGroup().getId().equals(event.getGroup().getId())) {
			return toResponse(event);
		}
		accessPolicy.requireEventAccess(event, current);
		return toResponse(event);
	}

	public EventResponse create(Long groupId, CreateEventRequest request) {
		var current = currentUserService.requireCurrent();
		accessPolicy.requireLeaderOrAdminForGroup(groupId, current);
		var group = groupService.requireActiveGroup(groupId);
		EventStatus status = request.getStatus() != null ? request.getStatus() : EventStatus.SCHEDULED;
		Event event = Event.builder()
				.group(group)
				.title(request.getTitle().trim())
				.location(trimToNull(request.getLocation()))
				.eventDate(request.getEventDate())
				.status(status)
				.build();
		return toResponse(eventRepository.save(event));
	}

	public EventResponse update(Long id, UpdateEventRequest request) {
		var current = currentUserService.requireCurrent();
		Event event = requireWithGroup(id);
		accessPolicy.requireLeaderOrAdminForGroup(event.getGroup().getId(), current);
		event.setTitle(request.getTitle().trim());
		event.setLocation(trimToNull(request.getLocation()));
		event.setEventDate(request.getEventDate());
		event.setStatus(request.getStatus());
		return toResponse(eventRepository.save(event));
	}

	public void delete(Long id) {
		var current = currentUserService.requireCurrent();
		Event event = requireWithGroup(id);
		accessPolicy.requireLeaderOrAdminForGroup(event.getGroup().getId(), current);
		eventRepository.delete(event);
	}

	public Event requireWithGroup(Long id) {
		return eventRepository.findByIdWithGroup(id).orElseThrow(() -> new NotFoundException("Evento não encontrado"));
	}

	private static String trimToNull(String s) {
		if (s == null) {
			return null;
		}
		String t = s.trim();
		return t.isEmpty() ? null : t;
	}

	private static EventResponse toResponse(Event event) {
		return new EventResponse(
				event.getId(),
				event.getGroup().getId(),
				event.getTitle(),
				event.getLocation(),
				event.getEventDate(),
				event.getStatus(),
				event.getCreatedAt()
		);
	}

	public void markAsReminded(List<Long> eventIds){
		for (Long events:eventIds){
			log.info("evento de ID {} marcado como reminded",events);
			eventRepository.markAsReminded(events);
		}

	}


}
