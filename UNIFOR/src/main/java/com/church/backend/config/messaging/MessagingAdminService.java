package com.church.backend.config.messaging;

import com.church.backend.config.messaging.dtoMessage.EventMessageDTO;
import com.church.backend.shared.security.AccessPolicy;
import com.church.backend.shared.security.CurrentUserService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class MessagingAdminService {

	private final CurrentUserService currentUserService;
	private final AccessPolicy accessPolicy;
	private final NotificationProducer notificationProducer;

	public void publishEventMessage(EventMessageDTO dto) {
		accessPolicy.requireAdmin(currentUserService.requireCurrent());
		notificationProducer.enviar(dto);
	}
}
