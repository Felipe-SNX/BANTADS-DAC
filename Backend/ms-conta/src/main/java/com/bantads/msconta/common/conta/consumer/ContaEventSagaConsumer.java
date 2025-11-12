package com.bantads.msconta.common.conta.consumer;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.springframework.amqp.rabbit.annotation.RabbitListener;
import org.springframework.amqp.support.AmqpHeaders;
import org.springframework.messaging.handler.annotation.Header;
import org.springframework.stereotype.Component;

import com.bantads.msconta.command.model.Conta;
import com.bantads.msconta.command.producer.ContaEventCQRSProducer;
import com.bantads.msconta.command.service.ContaCommandService;
import com.bantads.msconta.common.conta.dto.AutoCadastroInfo;
import com.bantads.msconta.common.conta.dto.DadosClienteConta;
import com.bantads.msconta.common.conta.dto.GerentesNumeroContasDto;
import com.bantads.msconta.common.conta.dto.PerfilInfo;
import com.bantads.msconta.common.conta.exception.ErroExecucaoSaga;
import com.bantads.msconta.common.conta.producer.ContaEventSagaProducer;
import com.bantads.msconta.common.saga.dto.DadoGerenteInsercao;
import com.bantads.msconta.common.saga.dto.Evento;
import com.bantads.msconta.common.saga.enums.EEventSource;
import com.bantads.msconta.common.saga.enums.ESaga;
import com.bantads.msconta.common.saga.enums.ESagaStatus;
import com.bantads.msconta.common.saga.enums.ETopics;
import com.bantads.msconta.config.rabbitmq.RabbitMQConstantes;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.node.ObjectNode;

import jakarta.transaction.Transactional;
import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@Slf4j
@Component
@AllArgsConstructor
public class ContaEventSagaConsumer {

    private final ContaCommandService contaCommandService;    
    private final ObjectMapper objectMapper;
    private final ContaEventSagaProducer contaEventProducer;
    private final ContaEventCQRSProducer contaEventCQRSProducer;
    
    @Transactional
    @RabbitListener(queues = RabbitMQConstantes.FILA_CONTA_CMD)
    public void handleAlteracoes(Evento evento, @Header(AmqpHeaders.RECEIVED_ROUTING_KEY) String routingKey){
        log.info("Evento recebido: {}", evento);
        log.info(routingKey);

        ESaga sagaType = evento.getSaga();

        if(routingKey.equals(ETopics.CMD_CONTA_CREATE.getTopic())){
            prosseguirTransacao(sagaType, evento);
        }
        else{
            compensarTransacao(sagaType, evento);
        }

        log.info("Banco de dados de cliente sincronizado com sucesso");
    }

    private void prosseguirTransacao(ESaga sagaType, Evento evento){
        try{
            JsonNode rootNode = objectMapper.readTree(evento.getPayload());

            switch(sagaType){
                case AUTOCADASTRO_SAGA:
                    List<GerentesNumeroContasDto> numeroContasGerente = contaCommandService.buscarNumeroDeContasPorGerente();
                    adicionarAoNode(rootNode, "numeroContasGerente", numeroContasGerente); 
                    atualizarPayload(evento, rootNode, sagaType);
                    evento.setStatus(ESagaStatus.SUCCESS);
                    publicarSucesso(evento);
                    break;
                case ALTERACAO_PERFIL_SAGA:
                    PerfilInfo perfilInfo = objectMapper.treeToValue(
                            rootNode.path("perfilInfo"), PerfilInfo.class
                    );
                    String cpf = objectMapper.treeToValue(rootNode.path("cpf"), String.class);
                    Conta contaAtualizada = contaCommandService.atualizarLimite(perfilInfo, cpf);
                    evento.setStatus(ESagaStatus.SUCCESS);
                    contaEventCQRSProducer.sendSyncReadDatabaseEvent(contaAtualizada);
                    publicarSucesso(evento);
                    break;
                case APROVAR_CLIENTE_SAGA:
                    DadosClienteConta dadosClienteConta = objectMapper.treeToValue(
                            rootNode.path("dadosClienteConta"), DadosClienteConta.class
                    );
                    Conta conta = contaCommandService.criarConta(dadosClienteConta);
                    evento.setStatus(ESagaStatus.SUCCESS);
                    contaEventCQRSProducer.sendSyncReadDatabaseEvent(conta);
                    publicarSucesso(evento);
                    break;
                case INSERCAO_GERENTE_SAGA:
                    DadoGerenteInsercao dadoGerenteInsercao = objectMapper.treeToValue(
                            rootNode.path("dadoGerenteInsercao"), DadoGerenteInsercao.class
                    );
                    contaCommandService.atribuirContas(dadoGerenteInsercao);
                    evento.setStatus(ESagaStatus.SUCCESS);
                    publicarSucesso(evento);
                    break;
                case REMOCAO_GERENTE_SAGA:
                    String cpfs = objectMapper.treeToValue(rootNode.path("cpf"), String.class);
                    contaCommandService.remanejarGerentes(cpfs);
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

        log.info("Banco de dados de conta sincronizado com sucesso");
    }

    private void compensarTransacao(ESaga sagaType, Evento evento){
        try{
            JsonNode rootNode = objectMapper.readTree(evento.getPayload());

            switch(sagaType){
                case REMOCAO_GERENTE_SAGA:
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
        evento.setSource(EEventSource.CONTA_SERVICE);
        contaEventProducer.sendEvent(ETopics.EVT_CONTA_SUCCESS, evento);
    }

    private void publicarFalha(Evento evento) {
        evento.setSource(EEventSource.CONTA_SERVICE);
        evento.setStatus(ESagaStatus.FAIL);
        contaEventProducer.sendEvent(ETopics.EVT_CONTA_FAIL, evento);
    }

    private void publicarCompensacaoSucesso(Evento evento) {
        evento.setSource(EEventSource.CONTA_SERVICE);
        evento.setStatus(ESagaStatus.COMPENSATE);
        contaEventProducer.sendEvent(ETopics.EVT_CONTA_SUCCESS, evento);
    }

    private void publicarCompensacaoFalha(Evento evento) {
        evento.setSource(EEventSource.CONTA_SERVICE);
        evento.setStatus(ESagaStatus.COMPENSATE_FAILED);
        contaEventProducer.sendEvent(ETopics.EVT_CONTA_FAIL, evento);
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
