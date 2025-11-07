package com.bantads.msauth.core.consumer;

import com.bantads.msauth.common.dto.Evento;
import com.bantads.msauth.common.enums.EEventSource;
import com.bantads.msauth.common.enums.ESaga;
import com.bantads.msauth.common.enums.ESagaStatus;
import com.bantads.msauth.common.enums.ETopics;
import com.bantads.msauth.config.rabbitmq.RabbitMQConstantes;
import com.bantads.msauth.core.dto.AutoCadastroInfo;
import com.bantads.msauth.core.dto.DadosClienteConta;
import com.bantads.msauth.core.producer.AuthEventProducer;
import com.bantads.msauth.core.service.AuthService;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.amqp.rabbit.annotation.RabbitListener;
import org.springframework.amqp.support.AmqpHeaders;
import org.springframework.messaging.handler.annotation.Header;
import org.springframework.stereotype.Component;

@Slf4j
@Component
@AllArgsConstructor
public class AuthEventConsumer {

    private final ObjectMapper objectMapper;
    private final AuthService authService;
    private final AuthEventProducer authEventProducer;

    @RabbitListener(queues = RabbitMQConstantes.FILA_AUTH)
    public void handleConsumer(Evento evento, @Header(AmqpHeaders.RECEIVED_ROUTING_KEY) String routingKey){
        log.info("Evento recebido: {}", evento);
        log.info(routingKey);

        ESaga sagaType = evento.getSaga();

        if(routingKey.equals(ETopics.CMD_AUTH_CREATE.getTopic())){
            log.info("entrei no if de ms-auth get topic");
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
                    JsonNode clienteNode = rootNode.path("autoCadastroInfo");
                    AutoCadastroInfo autoCadastroInfo = objectMapper.treeToValue(clienteNode, AutoCadastroInfo.class);
                    authService.cadastrarUsuarioCliente(autoCadastroInfo);
                    evento.setSource(EEventSource.AUTH_SERVICE);
                    evento.setStatus(ESagaStatus.SUCCESS);
                    authEventProducer.sendEvent(ETopics.EVT_AUTH_SUCCESS, evento);
                    break;
                case APROVAR_CLIENTE_SAGA:
                    JsonNode dadosClienteContaNode = rootNode.path("dadosClienteConta");
                    DadosClienteConta dadosClienteConta = objectMapper.treeToValue(dadosClienteContaNode, DadosClienteConta.class);
                    authService.enviarEmailAprovado(dadosClienteConta);
                    evento.setSource(EEventSource.AUTH_SERVICE);
                    evento.setStatus(ESagaStatus.FINISHED);
                    authEventProducer.sendEvent(ETopics.EVT_AUTH_SUCCESS, evento);
                    break;
                default:
                    break;
            }
        } catch(Exception e){
            log.info("Erro ocorreu em {} do tipo {}", sagaType, e);
            evento.setSource(EEventSource.AUTH_SERVICE);
            evento.setStatus(ESagaStatus.FAIL);
            authEventProducer.sendEvent(ETopics.EVT_AUTH_FAIL, evento);
        }
    }

    private void compensarTransacao(ESaga sagaType, Evento evento){
        try{
            JsonNode rootNode = objectMapper.readTree(evento.getPayload());

            switch(sagaType){
                case AUTOCADASTRO_SAGA:
                    break;
                case INSERCAO_GERENTE_SAGA:
                    break;
                case REMOCAO_GERENTE_SAGA:
                    break;
                default:
                    break;
            }
        } catch(Exception e){
            log.info("Erro ocorreu em {} do tipo {}", sagaType, e);
            evento.setSource(EEventSource.AUTH_SERVICE);
            evento.setStatus(ESagaStatus.COMPENSATE_FAILED);
            authEventProducer.sendEvent(ETopics.EVT_AUTH_FAIL, evento);
        }
    }
}
