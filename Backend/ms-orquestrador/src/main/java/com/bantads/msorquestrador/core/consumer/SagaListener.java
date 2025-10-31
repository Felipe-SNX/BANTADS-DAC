package com.bantads.msorquestrador.core.consumer;

import com.bantads.msorquestrador.config.rabbitmq.RabbitMQConstantes;
import com.bantads.msorquestrador.core.dto.Evento;
import com.bantads.msorquestrador.core.enums.ESaga;
import com.bantads.msorquestrador.core.enums.ETopics;
import com.bantads.msorquestrador.core.producer.SagaEventProducer;
import com.bantads.msorquestrador.core.saga.SagaHandler;
import com.bantads.msorquestrador.core.saga.SagaProcessor;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.amqp.rabbit.annotation.RabbitListener;
import org.springframework.stereotype.Component;

@Slf4j
@Component
@RequiredArgsConstructor
public class SagaListener {

    private final SagaProcessor sagaProcessor;
    private final SagaEventProducer sagaEventProducer;

    @RabbitListener(queues = RabbitMQConstantes.FILA_ORQUESTRADOR)
    public void handleEvents(Evento event) {
        log.info("Saga Listener recebeu um evento {}", event);
        try {
            ESaga sagaType = event.getSaga();

            Object[][] handler = SagaHandler.getHandler(sagaType);

            if (handler != null) {
                log.info("Processando SAGA do tipo: {}", sagaType);

                var sagaStep = sagaProcessor.processNextStep(event, handler);

                if(!(sagaStep.getProximoTopico().equals(ETopics.FINISH_SUCCESS)) && !(sagaStep.getProximoTopico().equals(ETopics.FINISH_FAIL)) ){
                    log.info("entrei no if de enviar evento");
                    sagaEventProducer.sendEvent(sagaStep.getProximoTopico(), sagaStep.getEventoParaEnviar());
                }
                else{
                    log.info("Saga {} finalizada, status final do evento {}", event.getSaga(), event);
                }

            } else {
                log.warn("Nenhum handler encontrado para a SAGA do tipo: {}", sagaType);
            }

        } catch (IllegalArgumentException e) {
            log.error("Saga inválida ou não reconhecida no evento: {}", event.getSaga());
        } catch (Exception e) {
            log.error("Erro inesperado ao processar o evento da SAGA: {}", event.getId(), e);
        }
    }
}