package com.bantads.msgerente.core.consumer;

import com.bantads.msgerente.common.dto.Evento;
import com.bantads.msgerente.common.enums.EEventSource;
import com.bantads.msgerente.common.enums.ESaga;
import com.bantads.msgerente.common.enums.ESagaStatus;
import com.bantads.msgerente.common.enums.ETopics;
import com.bantads.msgerente.config.rabbitmq.RabbitMQConstantes;
import com.bantads.msgerente.core.dto.DadoGerenteAtualizacao;
import com.bantads.msgerente.core.dto.DadoGerenteInsercao;
import com.bantads.msgerente.core.dto.GerenteNumeroContasDto;
import com.bantads.msgerente.core.dto.GerentesResponse;
import com.bantads.msgerente.core.dto.mapper.GerenteMapper;
import com.bantads.msgerente.core.exception.ErroExecucaoSaga;
import com.bantads.msgerente.core.producer.GerenteEventProducer;
import com.bantads.msgerente.core.service.GerenteService;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.node.ObjectNode;
import jakarta.transaction.Transactional;
import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.amqp.rabbit.annotation.RabbitListener;
import org.springframework.amqp.support.AmqpHeaders;
import org.springframework.messaging.handler.annotation.Header;
import org.springframework.stereotype.Component;
import java.util.List;

@Slf4j
@Component
@AllArgsConstructor
public class GerenteEventConsumer {

    private final GerenteService gerenteService;
    private final ObjectMapper objectMapper;
    private final GerenteEventProducer gerenteEventProducer;


    @Transactional
    @RabbitListener(queues = RabbitMQConstantes.FILA_GERENTES)
    public void handleConsumer(Evento evento, @Header(AmqpHeaders.RECEIVED_ROUTING_KEY) String routingKey){
        log.info("Evento recebido: {}", evento);
        log.info(routingKey);

        ESaga sagaType = evento.getSaga();

        if(routingKey.equals(ETopics.CMD_GERENTE_CREATE.getTopic())){
            prosseguirTransacao(sagaType, evento);
        }
        else{
            compensarTransacao(sagaType, evento);
        }

        log.info("Banco de dados de gerente sincronizado com sucesso");
    }

    private void prosseguirTransacao(ESaga sagaType, Evento evento){
        try{
            JsonNode rootNode = objectMapper.readTree(evento.getPayload());

            switch(sagaType){
                case AUTOCADASTRO_SAGA:
                    List<GerenteNumeroContasDto> dtoList = objectMapper.convertValue(
                            rootNode.path("numeroContasGerente"),
                            new TypeReference<List<GerenteNumeroContasDto>>() {}
                    );
                    GerenteNumeroContasDto gerenteEscolhido = gerenteService.selecionarGerente(dtoList);

                    adicionarAoNode(rootNode, "gerenteEscolhido", gerenteEscolhido); 
                    atualizarPayload(evento, rootNode, sagaType);

                    evento.setStatus(ESagaStatus.SUCCESS);
                    publicarSucesso(evento);
                    break;
                case INSERCAO_GERENTE_SAGA:
                    DadoGerenteInsercao dadoGerenteInsercao = objectMapper.treeToValue(
                            rootNode.path("dadoGerenteInsercao"), DadoGerenteInsercao.class
                    );
                    gerenteService.inserirGerente(dadoGerenteInsercao);
                    evento.setStatus(ESagaStatus.SUCCESS);
                    publicarSucesso(evento);
                    break;
                case REMOCAO_GERENTE_SAGA:
                    String cpf = objectMapper.treeToValue(rootNode.path("cpf"), String.class);
                    gerenteService.deletarGerentePorCpf(cpf);
                    evento.setStatus(ESagaStatus.FINISHED);
                    publicarSucesso(evento);
                    break;
                case ALTERAR_GERENTE_SAGA:
                    String cpfs = objectMapper.treeToValue(rootNode.path("cpf"), String.class);
                    DadoGerenteAtualizacao dadoGerenteAtualizacao = objectMapper.treeToValue(
                            rootNode.path("dadoGerenteAtualizacao"), DadoGerenteAtualizacao.class
                    );
                    GerentesResponse gerentesResponse = gerenteService.atualizarGerentePorCpf(dadoGerenteAtualizacao, cpfs);

                    adicionarAoNode(rootNode, "gerenteAtualizado", gerentesResponse);
                    atualizarPayload(evento, rootNode, sagaType);
                    evento.setStatus(ESagaStatus.SUCCESS);
                    publicarSucesso(evento);
                    break;
                default:
                    break;
            }
        } catch(Exception e){
            log.info("Erro ocorreu em {} do tipo {}", sagaType, e);
            publicarFalha(evento);
        }
    }

    private void compensarTransacao(ESaga sagaType, Evento evento){
        try{
            JsonNode rootNode = objectMapper.readTree(evento.getPayload());

            switch(sagaType){
                case INSERCAO_GERENTE_SAGA:
                    DadoGerenteInsercao dadoGerenteInsercao = objectMapper.treeToValue(
                            rootNode.path("dadoGerenteInsercao"), DadoGerenteInsercao.class
                    );
                    gerenteService.deletarGerentePorCpf(dadoGerenteInsercao.getCpf());
                    publicarCompensacaoSucesso(evento);
                    break;
                case AUTOCADASTRO_SAGA:
                    log.info("Não faz nada aqui, pois não foi alterado dados");
                    publicarCompensacaoSucesso(evento);
                    break;
                case ALTERAR_GERENTE_SAGA:
                    GerentesResponse gerentesResponse = objectMapper.treeToValue(
                            rootNode.path("gerenteAtualizado"), GerentesResponse.class
                    );
                    DadoGerenteAtualizacao dadosAntigos = GerenteMapper.gerentesResponseToDadoGerenteAtualizacao(gerentesResponse);
                    gerenteService.atualizarGerentePorCpf(dadosAntigos, gerentesResponse.getCpf());
                    publicarCompensacaoSucesso(evento);
                    break;
                default:
                    break;
            }
        } catch(Exception e){
            log.info("Erro ocorreu em {} do tipo {}", sagaType, e);
            publicarCompensacaoFalha(evento);
        }
    }

    private void publicarSucesso(Evento evento) {
        evento.setSource(EEventSource.GERENTE_SERVICE);
        gerenteEventProducer.sendEvent(ETopics.EVT_GERENTE_SUCCESS, evento);
    }

    private void publicarFalha(Evento evento) {
        evento.setSource(EEventSource.GERENTE_SERVICE);
        evento.setStatus(ESagaStatus.FAIL);
        gerenteEventProducer.sendEvent(ETopics.EVT_GERENTE_FAIL, evento);
    }

    private void publicarCompensacaoSucesso(Evento evento) {
        evento.setSource(EEventSource.GERENTE_SERVICE);
        evento.setStatus(ESagaStatus.COMPENSATE);
        gerenteEventProducer.sendEvent(ETopics.EVT_GERENTE_SUCCESS, evento);
    }

    private void publicarCompensacaoFalha(Evento evento) {
        evento.setSource(EEventSource.GERENTE_SERVICE);
        evento.setStatus(ESagaStatus.COMPENSATE_FAILED);
        gerenteEventProducer.sendEvent(ETopics.EVT_GERENTE_FAIL, evento);
    }

    private <T> void adicionarAoNode(JsonNode rootNode, String key, T value) {
        if (rootNode instanceof ObjectNode) {
            JsonNode node = objectMapper.valueToTree(value);
            ((ObjectNode) rootNode).set(key, node);
        } else {
            throw new ErroExecucaoSaga("Payload da saga (rootNode) não é um objeto JSON.");
        }
    }

    private void atualizarPayload(Evento evento, JsonNode rootNode, ESaga sagaType){
        try {
            evento.setPayload(objectMapper.writeValueAsString(rootNode));
        } catch (JsonProcessingException e) {
            log.error("Erro crítico ao atualizar o payload na saga {}: {}", sagaType, e.getMessage(), e);
            throw new ErroExecucaoSaga("Erro ao atualizar payload");
        }
    }
} 