package com.bantads.msgerente.core.producer;

import com.bantads.msgerente.config.rabbitmq.RabbitMQConstantes;
import com.bantads.msgerente.core.dto.Evento;
import com.bantads.msgerente.core.enums.ETopics;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.stereotype.Component;

@Slf4j
@Component
@RequiredArgsConstructor
public class GerenteEventProducer {

    private final RabbitTemplate rabbitTemplate;

    public void sendEvent(ETopics topic, Evento evento) {
        try {
            log.info("PRODUCER: Enviando evento para o exchange '{}' com a routing key '{}'. SagaId={}",
                    RabbitMQConstantes.NOME_EXCHANGE, topic.getTopic(), evento.getId());

            rabbitTemplate.convertAndSend(RabbitMQConstantes.NOME_EXCHANGE, topic.getTopic(), evento);

        } catch (Exception e) {
            log.error("PRODUCER: ERRO ao enviar evento para o tópico {}: SagaId={} | Erro: {}",
                    topic.getTopic(), evento.getId(), e.getMessage());
        }
    }
}
