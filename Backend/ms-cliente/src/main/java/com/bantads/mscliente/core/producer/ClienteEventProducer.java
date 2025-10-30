package com.bantads.mscliente.core.producer;

import com.bantads.mscliente.config.rabbitmq.RabbitMQConstantes;
import com.bantads.mscliente.core.dto.ClienteDto;
import com.bantads.mscliente.core.dto.Evento;
import com.bantads.mscliente.core.dto.Historico;
import com.bantads.mscliente.core.enums.EEventSource;
import com.bantads.mscliente.core.enums.ESaga;
import com.bantads.mscliente.core.enums.ESagaStatus;
import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.RequiredArgsConstructor;
import lombok.SneakyThrows;
import lombok.extern.slf4j.Slf4j;
import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.stereotype.Component;

import java.time.LocalDateTime;
import java.util.Collections;
import java.util.UUID;

@Slf4j
@Component
@RequiredArgsConstructor
public class ClienteEventProducer {

    private final RabbitTemplate rabbitTemplate;
    private final ObjectMapper objectMapper;

    @SneakyThrows
    public void publicarInicioSagaAutocadastro(ClienteDto clienteDto) {
        log.info("Publicando evento para iniciar a SAGA de Autocadastro");

        String sagaId = UUID.randomUUID().toString();

        var historicoInicial = Historico.builder()
                .source(EEventSource.CLIENTE_SERVICE)
                .status(ESagaStatus.SAGA_STARTED)
                .message("SAGA iniciada pelo MS-Cliente.")
                .createdAt(LocalDateTime.now())
                .build();

        var evento = Evento.builder()
                .id(sagaId)
                .payload(objectMapper.writeValueAsString(clienteDto))
                .status(ESagaStatus.SAGA_STARTED)
                .saga(ESaga.AUTOCADASTRO_SAGA)
                .source(EEventSource.CLIENTE_SERVICE)
                .createdAt(LocalDateTime.now())
                .eventoHistorico(Collections.singletonList(historicoInicial))
                .build();

        rabbitTemplate.convertAndSend(
                RabbitMQConstantes.NOME_EXCHANGE,
                RabbitMQConstantes.ROUTING_KEY_AUTOCADASTRO_START_SAGA,
                evento
        );

        log.info("Saga producer emitiu um evento {}", evento);
    }

    @SneakyThrows
    public void publicarInicioSagaAlteracaoPerfil(ClienteDto clienteDto) {
        log.info("Publicando evento para iniciar a SAGA de Alteracao Perfil");

        String sagaId = UUID.randomUUID().toString();

        var historicoInicial = Historico.builder()
                .source(EEventSource.CLIENTE_SERVICE)
                .status(ESagaStatus.SAGA_STARTED)
                .message("SAGA iniciada pelo MS-Cliente.")
                .createdAt(LocalDateTime.now())
                .build();

        var evento = Evento.builder()
                .id(sagaId)
                .payload(objectMapper.writeValueAsString(clienteDto))
                .status(ESagaStatus.SAGA_STARTED)
                .saga(ESaga.ALTERACAO_PERFIL_SAGA)
                .source(EEventSource.CLIENTE_SERVICE)
                .createdAt(LocalDateTime.now())
                .eventoHistorico(Collections.singletonList(historicoInicial))
                .build();

        rabbitTemplate.convertAndSend(
                RabbitMQConstantes.NOME_EXCHANGE,
                RabbitMQConstantes.ROUTING_KEY_ALTERACAO_PERFIL_START_SAGA,
                evento
        );

        log.info("Saga producer emitiu um evento {}", evento);
    }
}