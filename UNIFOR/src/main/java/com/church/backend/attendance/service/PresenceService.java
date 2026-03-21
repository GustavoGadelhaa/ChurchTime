package com.church.backend.attendance.service;

import com.church.backend.attendance.dto.PresenceDtos.PresenceResponse;
import com.church.backend.attendance.entity.Presence;
import com.church.backend.attendance.repository.PresenceRepository;
import com.church.backend.identity.repository.UserRepository;
import com.church.backend.shared.exception.ApiException;
import com.church.backend.shared.security.AccessPolicy;
import com.church.backend.shared.security.CurrentUserService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
@RequiredArgsConstructor
@Transactional
public class PresenceService {

	private final PresenceRepository presenceRepository;
	private final UserRepository userRepository;
	private final EventService eventService;
	private final CurrentUserService currentUserService;
	private final AccessPolicy accessPolicy;

	public PresenceResponse checkIn(Long eventId) {
		var current = currentUserService.requireCurrent();
		var event = eventService.requireWithGroup(eventId);
		accessPolicy.requireMemberCheckin(event, current);
		if (presenceRepository.existsByEventIdAndUserId(eventId, current.getId())) {
			throw new ApiException(HttpStatus.CONFLICT, "Check-in já registrado");
		}
		var userRef = userRepository.getReferenceById(current.getId());
		Presence presence = Presence.builder().event(event).user(userRef).build();
		presence = presenceRepository.save(presence);
		return toResponse(presence);
	}

	@Transactional(readOnly = true)
	public List<PresenceResponse> listByEvent(Long eventId) {
		var current = currentUserService.requireCurrent();
		var event = eventService.requireWithGroup(eventId);
		accessPolicy.requirePresencesRead(event, current);
		return presenceRepository.findByEventIdOrderByCheckedInAtAsc(eventId).stream().map(PresenceService::toResponse).toList();
	}

	private static PresenceResponse toResponse(Presence p) {
		return new PresenceResponse(
				p.getId(),
				p.getUser().getId(),
				p.getUser().getName(),
				p.getCheckedInAt()
		);
	}
}
