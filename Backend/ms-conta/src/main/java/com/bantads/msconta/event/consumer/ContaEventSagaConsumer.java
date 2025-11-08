package com.bantads.msconta.event.consumer;

import com.bantads.msconta.conta.command.model.Conta;
import com.bantads.msconta.event.dto.DadosClienteConta;
import com.bantads.msconta.event.producer.ContaEventCQRSProducer;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.springframework.amqp.rabbit.annotation.RabbitListener;
import org.springframework.amqp.support.AmqpHeaders;
import org.springframework.messaging.handler.annotation.Header;
import org.springframework.stereotype.Component;

import com.bantads.msconta.common.dto.DadoGerenteInsercao;
import com.bantads.msconta.common.dto.Evento;
import com.bantads.msconta.common.enums.EEventSource;
import com.bantads.msconta.common.enums.ESaga;
import com.bantads.msconta.common.enums.ESagaStatus;
import com.bantads.msconta.common.enums.ETopics;
import com.bantads.msconta.config.rabbitmq.RabbitMQConstantes;
import com.bantads.msconta.conta.command.service.ContaCommandService;
import com.bantads.msconta.conta.dto.GerentesNumeroContasDto;
import com.bantads.msconta.event.dto.AutoCadastroInfo;
import com.bantads.msconta.event.dto.PerfilInfo;
import com.bantads.msconta.event.producer.ContaEventSagaProducer;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.JsonMappingException;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;

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
                    JsonNode autoCadastroNode = rootNode.path("autoCadastroInfo");
                    AutoCadastroInfo autoCadastroInfo = objectMapper.treeToValue(autoCadastroNode, AutoCadastroInfo.class);
                    List<GerentesNumeroContasDto> numeroContasGerente = contaCommandService.buscarNumeroDeContasPorGerente();
                    Map<String, Object> novoMap = new HashMap<>();
                    novoMap.put("autoCadastroInfo", autoCadastroInfo);
                    novoMap.put("numeroContasGerente", numeroContasGerente);
                    evento.setPayload(objectMapper.writeValueAsString(novoMap));
                    evento.setSource(EEventSource.CONTA_SERVICE);
                    evento.setStatus(ESagaStatus.SUCCESS);
                    contaEventProducer.sendEvent(ETopics.EVT_CONTA_SUCCESS, evento);
                    break;
                case ALTERACAO_PERFIL_SAGA:
                    JsonNode perfilInfoNode = rootNode.path("perfilInfo");
                    JsonNode cpfNode = rootNode.path("cpf");
                    PerfilInfo perfilInfo = objectMapper.treeToValue(perfilInfoNode, PerfilInfo.class);
                    String cpf = objectMapper.treeToValue(cpfNode, String.class);
                    Conta contaAtualizada = contaCommandService.atualizarLimite(perfilInfo, cpf);
                    evento.setSource(EEventSource.CONTA_SERVICE);
                    evento.setStatus(ESagaStatus.SUCCESS);
                    contaEventCQRSProducer.sendSyncReadDatabaseEvent(contaAtualizada);
                    contaEventProducer.sendEvent(ETopics.EVT_CONTA_SUCCESS, evento);
                    break;
                case APROVAR_CLIENTE_SAGA:
                    JsonNode usuarioNode = rootNode.path("dadosClienteConta");
                    DadosClienteConta dadosClienteConta = objectMapper.treeToValue(usuarioNode, DadosClienteConta.class);
                    Conta conta = contaCommandService.criarConta(dadosClienteConta);
                    evento.setSource(EEventSource.CONTA_SERVICE);
                    evento.setStatus(ESagaStatus.SUCCESS);
                    contaEventCQRSProducer.sendSyncReadDatabaseEvent(conta);
                    contaEventProducer.sendEvent(ETopics.EVT_CONTA_SUCCESS, evento);
                    break;
                case INSERCAO_GERENTE_SAGA:
                    JsonNode gerenteNode = rootNode.path("dadoGerenteInsercao");
                    DadoGerenteInsercao dadoGerenteInsercao = objectMapper.treeToValue(gerenteNode, DadoGerenteInsercao.class);
                    contaCommandService.atribuirContas(dadoGerenteInsercao);
                    evento.setSource(EEventSource.CONTA_SERVICE);
                    evento.setStatus(ESagaStatus.SUCCESS);
                    contaEventProducer.sendEvent(ETopics.EVT_CONTA_SUCCESS, evento);
                    break;
                case REMOCAO_GERENTE_SAGA:
                    JsonNode cpfNodes = rootNode.path("cpf");
                    String cpfs = objectMapper.treeToValue(cpfNodes, String.class);
                    contaCommandService.remanejarGerentes(cpfs);
                    evento.setSource(EEventSource.CONTA_SERVICE);
                    evento.setStatus(ESagaStatus.SUCCESS);
                    contaEventProducer.sendEvent(ETopics.EVT_CONTA_SUCCESS, evento);
                    break;
                default:
                    break;
            }
        } catch(Exception e){
            log.info("Erro ocorreu em {} do tipo {}", sagaType, e);
            evento.setSource(EEventSource.CONTA_SERVICE);
            evento.setStatus(ESagaStatus.FAIL);
            contaEventProducer.sendEvent(ETopics.EVT_CONTA_FAIL, evento);
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
            evento.setSource(EEventSource.CONTA_SERVICE);
            evento.setStatus(ESagaStatus.COMPENSATE_FAILED);
            contaEventProducer.sendEvent(ETopics.EVT_CONTA_FAIL, evento);
        }
    }
}
