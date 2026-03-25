package com.church.backend.config.rabbitMQ;
import org.springframework.amqp.rabbit.connection.ConnectionFactory; // ✅ Spring AMQP
import org.springframework.amqp.support.converter.Jackson2JsonMessageConverter;
import org.springframework.amqp.support.converter.MessageConverter;
import org.springframework.amqp.core.Queue;
import org.springframework.amqp.rabbit.connection.ConnectionFactory;
import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.amqp.support.converter.Jackson2JsonMessageConverter;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;


@Configuration
public class RabbitConfig {

    public static final String FILA_EVENTOS = "fila.eventos";

    @Bean
    public Queue filaEventos() {
        return new Queue(FILA_EVENTOS, true);
    }

        @Bean
        public MessageConverter jsonMessageConverter(){
            return new Jackson2JsonMessageConverter();}

        @Bean
        public RabbitTemplate rabbitTemplate(ConnectionFactory connectionFactory){
            RabbitTemplate template=new RabbitTemplate(connectionFactory);
            template.setMessageConverter(jsonMessageConverter());
            return template;

        }
}