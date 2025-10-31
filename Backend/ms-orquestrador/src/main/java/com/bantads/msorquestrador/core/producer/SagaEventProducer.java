package com.bantads.msorquestrador.core.producer;

import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.stereotype.Component;

import com.bantads.msorquestrador.config.rabbitmq.RabbitMQConstantes;
import com.bantads.msorquestrador.core.dto.Evento;
import com.bantads.msorquestrador.core.enums.ETopics;

import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@Slf4j
@Component
@AllArgsConstructor
public class SagaEventProducer {

    private final RabbitTemplate rabbitTemplate;

    public void sendEvent(ETopics topic, Evento event) {
        try {
            log.info("SagaEventProducer.sendEvent: topic: {}, event: {}", topic, event);
            String routingKey = topic.getTopic();
            log.info("PRODUCER: Enviando evento para o exchange '{}' com a routing key '{}'. SagaId={}",
                    RabbitMQConstantes.NOME_EXCHANGE, routingKey, event.getId());
            
            rabbitTemplate.convertAndSend(RabbitMQConstantes.NOME_EXCHANGE, routingKey, event);

        } catch (Exception e) {
            log.error("PRODUCER: ERRO ao enviar evento para o tópico {}: SagaId={} | Erro: {}",
                    topic.getTopic(), event.getId(), e.getMessage());
        }
    }

}
