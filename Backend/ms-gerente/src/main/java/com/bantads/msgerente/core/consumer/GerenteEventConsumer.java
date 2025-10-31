package com.bantads.msgerente.core.consumer;

import com.bantads.msgerente.config.rabbitmq.RabbitMQConstantes;
import com.bantads.msgerente.core.dto.DadoGerente;
import com.bantads.msgerente.core.dto.DadoGerenteInsercao;
import com.bantads.msgerente.core.dto.Evento;
import com.bantads.msgerente.core.enums.EEventSource;
import com.bantads.msgerente.core.enums.ESaga;
import com.bantads.msgerente.core.enums.ESagaStatus;
import com.bantads.msgerente.core.enums.ETopics;
import com.bantads.msgerente.core.producer.GerenteEventProducer;
import com.bantads.msgerente.core.service.GerenteService;
import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import jakarta.transaction.Transactional;
import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.amqp.rabbit.annotation.RabbitListener;
import org.springframework.stereotype.Component;

import java.util.HashMap;

@Slf4j
@Component
@AllArgsConstructor
public class GerenteEventConsumer {

    private final GerenteService gerenteService;
    private final ObjectMapper objectMapper;
    private final GerenteEventProducer gerenteEventProducer;

    @Transactional
    @RabbitListener(queues = RabbitMQConstantes.FILA_GERENTES)
    public void handleConsumer(Evento evento){
        log.info("Evento recebido: {}", evento);

        ESaga sagaType = evento.getSaga();

        try{
         
            switch(sagaType){
                case AUTOCADASTRO_SAGA:
                    break;
                case INSERCAO_GERENTE_SAGA:
                    JsonNode rootNode = objectMapper.readTree(evento.getPayload());
                    JsonNode gerenteNode = rootNode.path("dadoGerenteInsercao");
                    DadoGerenteInsercao dadoGerenteInsercao = objectMapper.treeToValue(gerenteNode, DadoGerenteInsercao.class);
                    gerenteService.inserirGerente(dadoGerenteInsercao);
                    evento.setSource(EEventSource.GERENTE_SERVICE);
                    evento.setStatus(ESagaStatus.SUCCESS);
                    gerenteEventProducer.sendEvent(ETopics.EVT_GERENTE_SUCCESS, evento);
                    break;
                case REMOCAO_GERENTE_SAGA:
                    break;
                default:
                    break;
            }
        } catch(Exception e){
            log.info("Erro ocorreu em {} do tipo {}", sagaType, e);
            evento.setSource(EEventSource.GERENTE_SERVICE);
            evento.setStatus(ESagaStatus.FAIL);
            gerenteEventProducer.sendEvent(ETopics.EVT_GERENTE_FAIL, evento);
        }

        log.info("Banco de dados de gerente sincronizado com sucesso");
    }
}

