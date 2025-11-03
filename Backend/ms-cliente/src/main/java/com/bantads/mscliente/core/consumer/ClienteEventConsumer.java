package com.bantads.mscliente.core.consumer;

import org.springframework.amqp.rabbit.annotation.RabbitListener;
import org.springframework.stereotype.Component;
import com.bantads.mscliente.config.rabbitmq.RabbitMQConstantes;
import com.bantads.mscliente.core.dto.Evento;
import com.bantads.mscliente.core.enums.EEventSource;
import com.bantads.mscliente.core.enums.ESaga;
import com.bantads.mscliente.core.enums.ESagaStatus;
import com.bantads.mscliente.core.enums.ETopics;
import com.bantads.mscliente.core.producer.ClienteEventProducer;
import com.bantads.mscliente.core.service.ClienteService;
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
    public void handleAlteracoes(Evento evento){
        log.info("Evento recebido: {}", evento);

        ESaga sagaType = evento.getSaga();

        try{
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
            evento.setStatus(ESagaStatus.FAIL);
            clienteEventProducer.sendEvent(ETopics.EVT_CLIENTE_FAIL, evento);
        }

        log.info("Banco de dados de cliente sincronizado com sucesso");
    }
}
