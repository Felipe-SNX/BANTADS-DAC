package com.bantads.mscliente.core.consumer;

import org.springframework.amqp.rabbit.annotation.RabbitListener;
import org.springframework.amqp.support.AmqpHeaders;
import org.springframework.messaging.handler.annotation.Header;
import org.springframework.stereotype.Component;

import com.bantads.mscliente.common.dto.Evento;
import com.bantads.mscliente.common.enums.EEventSource;
import com.bantads.mscliente.common.enums.ESaga;
import com.bantads.mscliente.common.enums.ESagaStatus;
import com.bantads.mscliente.common.enums.ETopics;
import com.bantads.mscliente.config.rabbitmq.RabbitMQConstantes;
import com.bantads.mscliente.core.dto.AutoCadastroInfo;
import com.bantads.mscliente.core.dto.PerfilInfo;
import com.bantads.mscliente.core.producer.ClienteEventProducer;
import com.bantads.mscliente.core.service.ClienteService;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.node.ObjectNode;

import jakarta.transaction.Transactional;
import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@Slf4j
@Component
@AllArgsConstructor
public class ClienteEventConsumer {

    private final ClienteService clienteService;    
    private final ObjectMapper objectMapper;
    private final ClienteEventProducer clienteEventProducer;
    
    @Transactional
    @RabbitListener(queues = RabbitMQConstantes.FILA_CLIENTES)
    public void handleAlteracoes(Evento evento, @Header(AmqpHeaders.RECEIVED_ROUTING_KEY) String routingKey){
        log.info("Evento recebido: {}", evento);
        log.info(routingKey);

        ESaga sagaType = evento.getSaga();

        if(routingKey.equals(ETopics.CMD_CLIENTE_CREATE.getTopic())){
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
                    JsonNode clienteNode = rootNode.path("autoCadastroInfo");
                    AutoCadastroInfo autoCadastroInfo = objectMapper.treeToValue(clienteNode, AutoCadastroInfo.class);
                    clienteService.cadastrarCliente(autoCadastroInfo);                    
                    evento.setSource(EEventSource.CLIENTE_SERVICE);
                    evento.setStatus(ESagaStatus.SUCCESS);
                    clienteEventProducer.sendEvent(ETopics.EVT_CLIENTE_SUCCESS, evento);
                    break;
                case ALTERACAO_PERFIL_SAGA:
                    JsonNode perfilNode = rootNode.path("perfilInfo");
                    JsonNode cpfNode = rootNode.path("cpf");
                    PerfilInfo perfilInfo = objectMapper.treeToValue(perfilNode, PerfilInfo.class);
                    String cpf = objectMapper.treeToValue(cpfNode, String.class);
                    clienteService.atualizaCliente(perfilInfo, cpf);                    
                    evento.setSource(EEventSource.CLIENTE_SERVICE);
                    evento.setStatus(ESagaStatus.SUCCESS);
                    clienteEventProducer.sendEvent(ETopics.EVT_CLIENTE_SUCCESS, evento);
                    break;
                default:
                    break;
            }
        } catch(Exception e){
            log.info("Erro ocorreu em {} do tipo {}", sagaType, e);
            evento.setSource(EEventSource.CLIENTE_SERVICE);
            evento.setStatus(ESagaStatus.FAIL);
            clienteEventProducer.sendEvent(ETopics.EVT_CLIENTE_FAIL, evento);
        }
    }

    private void compensarTransacao(ESaga sagaType, Evento evento){
        try{
            JsonNode rootNode = objectMapper.readTree(evento.getPayload());

            switch(sagaType){
                case AUTOCADASTRO_SAGA:
                    break;
                case ALTERACAO_PERFIL_SAGA:
                    break;
                default:
                    break;
            }
        } catch(Exception e){
            log.info("Erro ocorreu em {} do tipo {}", sagaType, e);
            evento.setSource(EEventSource.CLIENTE_SERVICE);
            evento.setStatus(ESagaStatus.COMPENSATE_FAILED);
            clienteEventProducer.sendEvent(ETopics.EVT_CLIENTE_FAIL, evento);
        }
    }
}
