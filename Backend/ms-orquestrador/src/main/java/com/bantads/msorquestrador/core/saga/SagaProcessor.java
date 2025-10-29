package com.bantads.msorquestrador.core.saga;

import com.bantads.msorquestrador.core.enums.ETopics;
import com.bantads.msorquestrador.core.model.Evento;
import com.bantads.msorquestrador.core.model.Historico;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Optional;

@Slf4j
@Component
public class SagaProcessor {

    public SagaStep processNextStep(Evento incomingEvent, final Object[][] handler) {
        if (incomingEvent == null || incomingEvent.getSource() == null || incomingEvent.getStatus() == null) {
            throw new IllegalArgumentException("Evento, fonte e status não podem ser nulos.");
        }

        ETopics nextTopic = findTopicBySourceAndStatus(incomingEvent, handler);

        Evento eventToSend = addHistoryToEvent(incomingEvent, "Transição processada com sucesso.");

        logCurrentSaga(eventToSend, nextTopic);

        return new SagaStep(nextTopic, eventToSend);
    }

    private ETopics findTopicBySourceAndStatus(Evento event, final Object[][] handler) {
        return (ETopics) Arrays.stream(handler)
                .filter(row -> isEventSourceAndStatusValid(event, row))
                .map(i -> i[SagaAutocadastroHandler.TOPIC_INDEX])
                .findFirst()
                .orElseThrow(() -> new IllegalArgumentException("Tópico não encontrado para a transição: " + event));
    }

    private boolean isEventSourceAndStatusValid(Evento event, Object[] row) {
        var source = row[SagaAutocadastroHandler.EVENT_SOURCE_INDEX];
        var status = row[SagaAutocadastroHandler.SAGA_STATUS_INDEX];
        return event.getSource().equals(source) && event.getStatus().equals(status);
    }
    
    private Evento addHistoryToEvent(Evento event, String message) {
        var historico = Historico.builder()
                .source(event.getSource())
                .status(event.getStatus())
                .message(message)
                .createdAt(LocalDateTime.now())
                .build();

        var historicoList = Optional.ofNullable(event.getEventoHistorico()).orElse(new ArrayList<>());
        historicoList.add(historico);
        
        return Evento.builder()
                .id(event.getId())
                .payload(event.getPayload())
                .source(event.getSource()) 
                .status(event.getStatus())
                .createdAt(event.getCreatedAt())
                .eventoHistorico(historicoList) 
                .build();
    }

    private void logCurrentSaga(Evento event, ETopics nextTopic) {
        log.info("SAGA_PROCESS: SagaId={} | Source={} | Status={} | NextTopic={}",
                event.getId(), event.getSource(), event.getStatus(), nextTopic);
    }
}