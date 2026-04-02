package com.church.backend.config.messaging;

import com.church.backend.config.messaging.dtoMessage.EventMessageDTO;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/admin/messaging")
@RequiredArgsConstructor
public class MessagingAdminController {

	private final MessagingAdminService messagingAdminService;

	@PostMapping("/events")
	@ResponseStatus(HttpStatus.NO_CONTENT)
	public void publishEvent(@RequestBody @Valid EventMessageDTO body) {
		messagingAdminService.publishEventMessage(body);
	}
}
