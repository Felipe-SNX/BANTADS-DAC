package com.bantads.msorquestrador.core.producer;

import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.stereotype.Component;

import com.bantads.msorquestrador.config.rabbitmq.RabbitMQConstantes;
import com.bantads.msorquestrador.core.enums.ETopics;
import com.bantads.msorquestrador.core.model.Evento;

import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@Slf4j
@Component
@AllArgsConstructor
public class SagaEventProducer {

    private RabbitTemplate rabbitTemplate;

    public void sendEvent(ETopics topic, Evento event) {
        try {
            String routingKey = topic.toString();
            log.info("PRODUCER: Enviando evento para o exchange '{}' com a routing key '{}'. SagaId={}",
                    RabbitMQConstantes.NOME_EXCHANGE, routingKey, event.getId());
            
            rabbitTemplate.convertAndSend(RabbitMQConstantes.NOME_EXCHANGE, routingKey, event);

        } catch (Exception e) {
            log.error("PRODUCER: ERRO ao enviar evento para o tópico {}: SagaId={} | Erro: {}",
                      topic.toString(), event.getId(), e.getMessage());
        }
    }

}
