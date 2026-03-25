package com.church.backend.shared.jobReminder;

import com.church.backend.attendance.entity.Event;
import com.church.backend.attendance.repository.EventRepository;
import com.church.backend.attendance.service.EventService;
import com.church.backend.config.messaging.NotificationProducer;
import com.church.backend.config.messaging.dtoMessage.EventMessageDTO;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

import java.time.Instant;
import java.time.temporal.ChronoUnit;
import java.util.ArrayList;
import java.util.List;

@Component
@RequiredArgsConstructor
@Slf4j
public class DailyJob {

	private final EventRepository eventRepository;
	private final NotificationProducer notificationProducer;
	private final EventService eventService;

	/**
	 * A cada 10 minutos (UTC): marca {@code reminded} nos eventos elegíveis e usa a lista retornada
	 * apenas dentro deste método (assinatura {@code void}).
	 */
	@Scheduled(fixedRate = 20000) // 10 segundos em milissegundos
	public void dailyJobReminder() {
		Instant now = Instant.now();
		log.info("Buscando eventos entre {} e {}", now, now.plus(6, ChronoUnit.HOURS));
		List<EventMessageDTO> lembretes = eventRepository.findUpcomingWithinSixHours();


		log.info("Eventos encontrados: {}", lembretes.size());
		if (lembretes.isEmpty()) {
			log.debug("DailyJob: nenhum evento para lembrete nesta execução.");
			return;
		}

		List<Long> eventIds = lembretes.stream().map(EventMessageDTO::getId).toList();
		eventService.markAsReminded(eventIds);

		for (EventMessageDTO event : lembretes) {
			notificationProducer.enviar(event);

		}
	}
}
