package com.bantads.msgerente.core.consumer;

import com.bantads.msgerente.config.rabbitmq.RabbitMQConstantes;
import com.bantads.msgerente.core.dto.DadoGerenteInsercao;
import com.bantads.msgerente.core.dto.Evento;
import com.bantads.msgerente.core.dto.GerenteNumeroContasDto;
import com.bantads.msgerente.core.enums.EEventSource;
import com.bantads.msgerente.core.enums.ESaga;
import com.bantads.msgerente.core.enums.ESagaStatus;
import com.bantads.msgerente.core.enums.ETopics;
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
                    JsonNode numeroContasGerenteNode = rootNode.path("numeroContasGerente");
                    List<GerenteNumeroContasDto> gerenteNumeroContasDtoList =
                            objectMapper.convertValue(
                                    numeroContasGerenteNode,
                                    new TypeReference<List<GerenteNumeroContasDto>>() {}
                            );
                    GerenteNumeroContasDto gerenteNumeroContasDto = gerenteService.selecionarGerente(gerenteNumeroContasDtoList);
                    //Verifica se o retorno não é nulo
                    if (gerenteNumeroContasDto == null) {
                        evento.setSource(EEventSource.GERENTE_SERVICE);
                        evento.setStatus(ESagaStatus.FAIL);
                        break;
                    }

                    JsonNode gerenteEscolhidoNode = objectMapper.valueToTree(gerenteNumeroContasDto);

                    if (rootNode instanceof ObjectNode) {
                        ((ObjectNode) rootNode).set("gerenteEscolhido", gerenteEscolhidoNode);
                    } else {
                        throw new RuntimeException("Payload da saga (rootNode) não é um objeto JSON.");
                    }

                    try {
                        evento.setPayload(objectMapper.writeValueAsString(rootNode));
                    } catch (JsonProcessingException e) {
                        evento.setSource(EEventSource.GERENTE_SERVICE);
                        evento.setStatus(ESagaStatus.FAIL);
                        break;
                    }
                    evento.setSource(EEventSource.GERENTE_SERVICE);
                    evento.setStatus(ESagaStatus.SUCCESS);
                    gerenteEventProducer.sendEvent(ETopics.EVT_GERENTE_SUCCESS, evento);
                    break;
                case INSERCAO_GERENTE_SAGA:
                    JsonNode gerenteNode = rootNode.path("dadoGerenteInsercao");
                    DadoGerenteInsercao dadoGerenteInsercao = objectMapper.treeToValue(gerenteNode, DadoGerenteInsercao.class);
                    gerenteService.inserirGerente(dadoGerenteInsercao);
                    evento.setSource(EEventSource.GERENTE_SERVICE);
                    evento.setStatus(ESagaStatus.SUCCESS);
                    gerenteEventProducer.sendEvent(ETopics.EVT_GERENTE_SUCCESS, evento);
                    break;
                case REMOCAO_GERENTE_SAGA:
                    JsonNode cpfNode = rootNode.path("cpf");
                    String cpf = objectMapper.treeToValue(cpfNode, String.class);
                    gerenteService.deletarGerentePorCpf(cpf);
                    evento.setSource(EEventSource.GERENTE_SERVICE);
                    evento.setStatus(ESagaStatus.FINISHED);
                    gerenteEventProducer.sendEvent(ETopics.EVT_GERENTE_SUCCESS, evento);
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
    }

    private void compensarTransacao(ESaga sagaType, Evento evento){
        try{
            JsonNode rootNode = objectMapper.readTree(evento.getPayload());

            switch(sagaType){
                case INSERCAO_GERENTE_SAGA:
                    JsonNode gerenteNode = rootNode.path("dadoGerenteInsercao");
                    DadoGerenteInsercao dadoGerenteInsercao = objectMapper.treeToValue(gerenteNode, DadoGerenteInsercao.class);
                    gerenteService.deletarGerentePorCpf(dadoGerenteInsercao.getCpf());
                    evento.setSource(EEventSource.GERENTE_SERVICE);
                    evento.setStatus(ESagaStatus.COMPENSATE);
                    gerenteEventProducer.sendEvent(ETopics.EVT_GERENTE_SUCCESS, evento);
                    break;
                case REMOCAO_GERENTE_SAGA:
                    JsonNode cpfNode = rootNode.path("cpf");
                    String cpf = objectMapper.treeToValue(cpfNode, String.class);
                    gerenteService.deletarGerentePorCpf(cpf);
                    evento.setSource(EEventSource.GERENTE_SERVICE);
                    evento.setStatus(ESagaStatus.COMPENSATE);
                    gerenteEventProducer.sendEvent(ETopics.EVT_GERENTE_SUCCESS, evento);
                    break;
                default:
                    break;
            }
        } catch(Exception e){
            log.info("Erro ocorreu em {} do tipo {}", sagaType, e);
            evento.setSource(EEventSource.GERENTE_SERVICE);
            evento.setStatus(ESagaStatus.COMPENSATE_FAILED);
            gerenteEventProducer.sendEvent(ETopics.EVT_GERENTE_FAIL, evento);
        }
    }
}

