package com.bantads.msorquestrador.core.service;

import java.time.LocalDateTime;
import java.util.*;

import com.bantads.msorquestrador.core.exception.ErroAoConverterJsonException;
import org.springframework.stereotype.Service;

import com.bantads.msorquestrador.core.dto.AutoCadastroInfo;
import com.bantads.msorquestrador.core.dto.DadoGerenteInsercao;
import com.bantads.msorquestrador.core.dto.Evento;
import com.bantads.msorquestrador.core.dto.Historico;
import com.bantads.msorquestrador.core.dto.PerfilInfo;
import com.bantads.msorquestrador.core.enums.EEventSource;
import com.bantads.msorquestrador.core.enums.ESaga;
import com.bantads.msorquestrador.core.enums.ESagaStatus;
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

    public void iniciarSagaAlterarPerfil(PerfilInfo perfilInfo, String cpf) {
        log.info("Saga de alterar perfil iniciada para o cliente: {}", perfilInfo.nome());


        Map<String, Object> alterarPerfilInfo = new HashMap<>();
        alterarPerfilInfo.put("cpf", cpf);
        alterarPerfilInfo.put("perfilInfo", perfilInfo);

        try {
            Evento evento = criarEvento(alterarPerfilInfo, ESaga.ALTERACAO_PERFIL_SAGA, EEventSource.ORQUESTRADOR);
            publicarEvento(evento);
        } catch (JsonProcessingException e) {
            throw new ErroAoConverterJsonException("Saga Alterar Perfil", e.getMessage());
        }
    }

    public void iniciarSagaInserirGerente(DadoGerenteInsercao dadoGerenteInsercao) {
        log.info("Saga de inserir gerente iniciada para o gerente: {}", dadoGerenteInsercao.nome());

        Map<String, Object> inserirGerenteInfo = new HashMap<>();
        inserirGerenteInfo.put("dadoGerenteInsercao", dadoGerenteInsercao);

        try {
            Evento evento = criarEvento(inserirGerenteInfo, ESaga.INSERCAO_GERENTE_SAGA, EEventSource.ORQUESTRADOR);
            publicarEvento(evento);
        } catch (JsonProcessingException e) {
            throw new ErroAoConverterJsonException("Saga Inserir Gerente", e.getMessage());
        }
    }

    public void iniciarSagaRemoverGerente(String cpf) {
        log.info("Saga de alterar perfil iniciada para o gerente de cpf: {}", cpf);

        Map<String, Object> removerGerenteInfo = new HashMap<>();
        removerGerenteInfo.put("cpf", cpf);

        try {
            Evento evento = criarEvento(removerGerenteInfo, ESaga.REMOCAO_GERENTE_SAGA, EEventSource.ORQUESTRADOR);
            publicarEvento(evento);
        } catch (JsonProcessingException e) {
            throw new ErroAoConverterJsonException("Saga Remover Gerente", e.getMessage());
        }
    }

    private String gerarId() {
        return UUID.randomUUID().toString();
    }


    private Evento criarEvento(Map<String, Object> payload, ESaga saga, EEventSource source) throws JsonProcessingException{
        String sagaId = gerarId();

        return Evento.builder()
                .id(sagaId)
                .payload(objectMapper.writeValueAsString(payload))
                .status(ESagaStatus.SAGA_STARTED)
                .saga(saga)
                .source(source)
                .createdAt(LocalDateTime.now())
                .build();
    }

    private void publicarEvento(Evento evento){
        ESaga sagaType = evento.getSaga();
        Object[][] handler = SagaHandler.getHandler(sagaType);
        var sagaStep = sagaProcessor.processNextStep(evento, handler);
        sagaEventProducer.sendEvent(sagaStep.getProximoTopico(), sagaStep.getEventoParaEnviar());
    }
}
