package com.bantads.msconta.common.conta.producer;

import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.stereotype.Component;

import com.bantads.msconta.common.saga.dto.Evento;
import com.bantads.msconta.common.saga.enums.ETopics;
import com.bantads.msconta.config.rabbitmq.RabbitMQConstantes;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@Slf4j
@Component
@RequiredArgsConstructor
public class ContaEventSagaProducer {

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
