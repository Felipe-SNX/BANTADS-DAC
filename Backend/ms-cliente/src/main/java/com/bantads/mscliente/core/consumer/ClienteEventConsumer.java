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
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;

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

        if(routingKey.equals(ETopics.CMD_CLIENTE_COMPENSATE.getTopic())){
            compensarTransacao(sagaType, evento);
        }
        else{
            prosseguirTransacao(sagaType, evento, routingKey);
        }

        log.info("Banco de dados de cliente sincronizado com sucesso");
    }

    private void prosseguirTransacao(ESaga sagaType, Evento evento, String routingKey){
        try{
            JsonNode rootNode = objectMapper.readTree(evento.getPayload());

            switch(sagaType){
                case AUTOCADASTRO_SAGA:
                    Evento eventoAtualizado = new Evento();

                    if(routingKey.equals(ETopics.CMD_CLIENTE_CREATE.getTopic())){
                        eventoAtualizado = criarClienteAutocadastro(evento);  
                    }     
                    else{
                        eventoAtualizado = atribuirGerente(evento);
                    }

                    clienteEventProducer.sendEvent(ETopics.EVT_CLIENTE_SUCCESS, eventoAtualizado); 
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

    private Evento criarClienteAutocadastro(Evento evento) throws JsonProcessingException{
        JsonNode rootNode = objectMapper.readTree(evento.getPayload());
        JsonNode autoCadastroNode = rootNode.path("autoCadastroInfo");
        AutoCadastroInfo autoCadastroInfo = objectMapper.treeToValue(autoCadastroNode, AutoCadastroInfo.class);
        clienteService.cadastrarCliente(autoCadastroInfo); 
        return marcarComoSucesso(evento);
    }

    private Evento atribuirGerente(Evento evento) throws JsonProcessingException, IllegalArgumentException{
        JsonNode rootNode = objectMapper.readTree(evento.getPayload());
        JsonNode autoCadastroNode = rootNode.path("autoCadastroInfo");
        AutoCadastroInfo autoCadastroInfo = objectMapper.treeToValue(autoCadastroNode, AutoCadastroInfo.class);
        String cpfCliente = autoCadastroInfo.getCpf();
        JsonNode cpfGerenteNode = rootNode.path("cpfGerente");
        String cpfGerente = objectMapper.treeToValue(cpfGerenteNode, String.class);
        clienteService.atribuirGerente(cpfCliente, cpfGerente);
        return marcarComoSucesso(evento);
    }

    private Evento marcarComoSucesso(Evento evento){
        evento.setSource(EEventSource.CLIENTE_SERVICE);
        evento.setStatus(ESagaStatus.SUCCESS);
        return evento;
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
