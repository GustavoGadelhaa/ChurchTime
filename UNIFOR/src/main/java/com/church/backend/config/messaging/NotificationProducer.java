package com.church.backend.config.messaging;

import com.church.backend.attendance.entity.Event;
import com.church.backend.config.messaging.dtoMessage.EventMessageDTO;
import com.church.backend.config.rabbitMQ.RabbitConfig;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.stereotype.Service;
@Slf4j
@Service
@RequiredArgsConstructor
public class NotificationProducer {

    private final RabbitTemplate rabbitTemplate;

    public void enviar(EventMessageDTO dto) {
        log.info("enviado notificação {} pro microserviço",dto.getTitle());
        rabbitTemplate.convertAndSend(RabbitConfig.FILA_EVENTOS, dto);
    }
}