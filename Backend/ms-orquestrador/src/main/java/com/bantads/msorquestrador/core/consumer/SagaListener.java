package com.bantads.msorquestrador.core.consumer;

import com.bantads.msorquestrador.config.rabbitmq.RabbitMQConstantes;
import com.bantads.msorquestrador.core.model.Evento;
import com.bantads.msorquestrador.core.producer.SagaEventProducer;
import com.bantads.msorquestrador.core.saga.SagaAutocadastroHandler;
import com.bantads.msorquestrador.core.saga.SagaProcessor;
import lombok.RequiredArgsConstructor;
import org.springframework.amqp.rabbit.annotation.RabbitListener;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
public class SagaListener {

    private final SagaProcessor sagaProcessor;
    private final SagaEventProducer sagaEventProducer;

    @RabbitListener(queues = RabbitMQConstantes.FILA)
    public void handleEvents(Evento event) {
        try {
            if (isAutocadastroSaga(event)) {
                var sagaStep = sagaProcessor.processNextStep(event, SagaAutocadastroHandler.SAGA_AUTOCADASTRO_HANDLER);

                sagaEventProducer.sendEvent(sagaStep.getProximoTopico(), sagaStep.getEventoParaEnviar());
            }

        } catch (Exception e) {
            // Tratar erro...
        }
    }

    private boolean isAutocadastroSaga(Evento event) {
        return true; 
    }
}
