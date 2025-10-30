package com.bantads.mscliente.core.producer;

import com.bantads.mscliente.config.rabbitmq.RabbitMQConstantes;
import com.bantads.mscliente.core.dto.ClienteDto;
import com.bantads.mscliente.core.dto.Evento;
import com.bantads.mscliente.core.dto.Historico;
import com.bantads.mscliente.core.enums.EEventSource;
import com.bantads.mscliente.core.enums.ESaga;
import com.bantads.mscliente.core.enums.ESagaStatus;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.RequiredArgsConstructor;
import lombok.SneakyThrows;
import lombok.extern.slf4j.Slf4j;
import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.stereotype.Component;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Optional;
import java.util.UUID;

@Slf4j
@Component
@RequiredArgsConstructor
public class ClienteEventProducer {

    private final RabbitTemplate rabbitTemplate;
    private final ObjectMapper objectMapper;
    private static final EEventSource CURRENT_SOURCE = EEventSource.CLIENTE_SERVICE;

    @SneakyThrows
    public void publicarInicioSagaAutocadastro(ClienteDto clienteDto) {
        log.info("Publicando evento para iniciar a SAGA de Autocadastro");

        Historico historicoInicial = criarHistorico(CURRENT_SOURCE, ESagaStatus.SAGA_STARTED, "SAGA de autocadastro iniciada pelo MS-Cliente.");
        Evento evento = criarEvento(clienteDto, historicoInicial);

        publicarEvento(evento, RabbitMQConstantes.ROUTING_KEY_AUTOCADASTRO_START_SAGA);

        log.info("Saga producer emitiu um evento {}", evento);
    }

    @SneakyThrows
    public void publicarInicioSagaAlteracaoPerfil(ClienteDto clienteDto) {
        log.info("Publicando evento para iniciar a SAGA de Alteracao Perfil");

        Historico historicoInicial = criarHistorico(CURRENT_SOURCE, ESagaStatus.SAGA_STARTED,"SAGA de alteração de perfil iniciada pelo MS-Cliente.");
        Evento evento = criarEvento(clienteDto, historicoInicial);

        publicarEvento(evento, RabbitMQConstantes.ROUTING_KEY_ALTERACAO_PERFIL_START_SAGA);

        log.info("Saga producer emitiu um evento {}", evento);
    }

    public void publicarEventoFalha(Evento event, String routingKey){
        updateEvent(event, ESagaStatus.FAIL, "Falha na operação do MS-Cliente: ");

        log.info("PRODUCER DE RESPOSTA: Enviando evento de FALHA. Rota: '{}', SagaId: {}", routingKey, event.getId());

        rabbitTemplate.convertAndSend(RabbitMQConstantes.NOME_EXCHANGE, routingKey, event);
    }

    private void updateEvent(Evento event, ESagaStatus status, String message) {
   
        event.setSource(CURRENT_SOURCE);
        event.setStatus(status);

        var historico = Historico.builder()
                .source(CURRENT_SOURCE)
                .status(status)
                .message(message)
                .createdAt(LocalDateTime.now())
                .build();
        
        var historicoList = Optional.ofNullable(event.getEventoHistorico()).orElse(new ArrayList<>());
        historicoList.add(historico);
        event.setEventoHistorico(historicoList);
    }

    private String gerarId() {
        return UUID.randomUUID().toString();
    }

    private Historico criarHistorico(EEventSource source, ESagaStatus status, String message){
        return Historico.builder()
                .source(source)
                .status(status)
                .message(message)
                .createdAt(LocalDateTime.now())
                .build();
    }

    private Evento criarEvento(ClienteDto clienteDto, Historico historicoInicial) throws JsonProcessingException{
        String sagaId = gerarId();

        return Evento.builder()
                .id(sagaId)
                .payload(objectMapper.writeValueAsString(clienteDto))
                .status(ESagaStatus.SAGA_STARTED)
                .saga(ESaga.ALTERACAO_PERFIL_SAGA)
                .source(EEventSource.CLIENTE_SERVICE)
                .createdAt(LocalDateTime.now())
                .eventoHistorico(Collections.singletonList(historicoInicial))
                .build();
    }

    private void publicarEvento(Evento evento, String routingKey) {
        rabbitTemplate.convertAndSend(
                RabbitMQConstantes.NOME_EXCHANGE,
                routingKey,
                evento
        );
    }

}