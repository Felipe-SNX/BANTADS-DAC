package com.bantads.msorquestrador.core.service;

import java.time.LocalDateTime;
import java.util.Collections;
import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

import org.springframework.stereotype.Service;

import com.bantads.msorquestrador.core.dto.AutoCadastroInfo;
import com.bantads.msorquestrador.core.dto.DadoGerenteInsercao;
import com.bantads.msorquestrador.core.dto.PerfilInfo;
import com.bantads.msorquestrador.core.enums.EEventSource;
import com.bantads.msorquestrador.core.enums.ESaga;
import com.bantads.msorquestrador.core.enums.ESagaStatus;
import com.bantads.msorquestrador.core.model.Evento;
import com.bantads.msorquestrador.core.model.Historico;
import com.bantads.msorquestrador.core.producer.SagaEventProducer;
import com.bantads.msorquestrador.core.saga.SagaHandler;
import com.bantads.msorquestrador.core.saga.SagaProcessor;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;

import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@Slf4j
@Service
@AllArgsConstructor
public class SagaService {

    private final ObjectMapper objectMapper;
    private final SagaProcessor sagaProcessor;
    private final SagaEventProducer sagaEventProducer;

    public void iniciarSagaAutocadastro(AutoCadastroInfo autoCadastroInfo) {
        log.info("Saga de autocadastro iniciada para o cliente: {}", autoCadastroInfo.nome());

        Historico historicoInicial = criarHistorico(EEventSource.ORQUESTRADOR, ESagaStatus.SAGA_STARTED, "Saga de autocadastro iniciada");
        Map<String, Object> autocadastro = new HashMap<>();
        autocadastro.put("autoCadastroInfo", autoCadastroInfo);

        try {
            Evento evento = criarEvento(autocadastro, historicoInicial, ESaga.AUTOCADASTRO_SAGA, EEventSource.ORQUESTRADOR);
            publicarEvento(evento);
        } catch (JsonProcessingException e) {
            e.printStackTrace();
        }
    }

    public void iniciarSagaAlterarPerfil(PerfilInfo perfilInfo, String cpf) {
        log.info("Saga de alterar perfil iniciada para o cliente: {}", perfilInfo.nome());

        Historico historicoInicial = criarHistorico(EEventSource.ORQUESTRADOR, ESagaStatus.SAGA_STARTED, "Saga de alterar perfil iniciada");

        Map<String, Object> alterarPerfilInfo = new HashMap<>();
        alterarPerfilInfo.put("cpf", cpf);
        alterarPerfilInfo.put("perfilInfo", perfilInfo);

        try {
            Evento evento = criarEvento(alterarPerfilInfo, historicoInicial, ESaga.ALTERACAO_PERFIL_SAGA, EEventSource.ORQUESTRADOR);
            publicarEvento(evento);
        } catch (JsonProcessingException e) {
            e.printStackTrace();
        }
    }

    public void iniciarSagaInserirGerente(DadoGerenteInsercao dadoGerenteInsercao) {
        log.info("Saga de alterar perfil iniciada para o gerente: {}", dadoGerenteInsercao.nome());

        Historico historicoInicial = criarHistorico(EEventSource.ORQUESTRADOR, ESagaStatus.SAGA_STARTED, "Saga de inserir gerente iniciada");

        Map<String, Object> inserirGerenteInfo = new HashMap<>();
        inserirGerenteInfo.put("dadoGerenteInsercao", dadoGerenteInsercao);

        try {
            Evento evento = criarEvento(inserirGerenteInfo, historicoInicial, ESaga.INSERCAO_GERENTE_SAGA, EEventSource.ORQUESTRADOR);
            publicarEvento(evento);
        } catch (JsonProcessingException e) {
            e.printStackTrace();
        }
    }

    public void iniciarSagaRemoverGerente(String cpf) {
        log.info("Saga de alterar perfil iniciada para o gerente de cpf: {}", cpf);

        Historico historicoInicial = criarHistorico(EEventSource.ORQUESTRADOR, ESagaStatus.SAGA_STARTED, "Saga de remover gerente iniciada");

        Map<String, Object> removerGerenteInfo = new HashMap<>();
        removerGerenteInfo.put("cpf", cpf);

        try {
            Evento evento = criarEvento(removerGerenteInfo, historicoInicial, ESaga.REMOCAO_GERENTE_SAGA, EEventSource.ORQUESTRADOR);
            publicarEvento(evento);
        } catch (JsonProcessingException e) {
            e.printStackTrace();
        }
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

    private Evento criarEvento(Map<String, Object> payload, Historico historicoInicial, ESaga saga, EEventSource source) throws JsonProcessingException{
        String sagaId = gerarId();

        return Evento.builder()
                .id(sagaId)
                .payload(objectMapper.writeValueAsString(payload))
                .status(ESagaStatus.SAGA_STARTED)
                .saga(saga)
                .source(source)
                .createdAt(LocalDateTime.now())
                .eventoHistorico(Collections.singletonList(historicoInicial))
                .build();
    }

    private void publicarEvento(Evento evento){
        ESaga sagaType = evento.getSaga();
        Object[][] handler = SagaHandler.getHandler(sagaType);
        var sagaStep = sagaProcessor.processNextStep(evento, handler);
        sagaEventProducer.sendEvent(sagaStep.getProximoTopico(), sagaStep.getEventoParaEnviar());
    }
}
