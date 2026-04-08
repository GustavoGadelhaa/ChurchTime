package com.church.backend.config.messaging;

import com.church.backend.config.messaging.dtoMessage.EventMessageDTO;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/admin/messaging")
@RequiredArgsConstructor
@Slf4j
public class MessagingAdminController {

	private final MessagingAdminService messagingAdminService;

	@PostMapping("/events")
	@ResponseStatus(HttpStatus.NO_CONTENT)
	public void publishEvent(@RequestBody @Valid EventMessageDTO body) {
		log.info("[MESSAGING] POST /api/admin/messaging/events - Event: {}, Timestamp: {}", 
				body.getTitle(), java.time.LocalDateTime.now());
		messagingAdminService.publishEventMessage(body);
		log.info("[MESSAGING] POST /api/admin/messaging/events - PUBLISHED - Timestamp: {}", 
				java.time.LocalDateTime.now());
	}
}
