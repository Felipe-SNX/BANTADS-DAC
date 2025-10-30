package com.bantads.mscliente.core.producer;

import com.bantads.mscliente.config.rabbitmq.RabbitMQConstantes;
import com.bantads.mscliente.core.dto.Evento;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.stereotype.Component;

@Slf4j
@Component
@RequiredArgsConstructor
public class ClienteEventProducer {

    private RabbitTemplate rabbitTemplate;

    public void sendEvent(String routingKey, Evento event) {
        try {
            log.info("PRODUCER: Enviando evento para o exchange '{}' com a routing key '{}'. SagaId={}",
                    RabbitMQConstantes.NOME_EXCHANGE, routingKey, event.getId());
            
            rabbitTemplate.convertAndSend(RabbitMQConstantes.NOME_EXCHANGE, routingKey, event);

        } catch (Exception e) {
            log.error("PRODUCER: ERRO ao enviar evento para o tópico {}: SagaId={} | Erro: {}",
                    routingKey, event.getId(), e.getMessage());
        }
    }

}