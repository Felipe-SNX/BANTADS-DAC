package com.bantads.msconta.event.consumer;

import org.springframework.amqp.rabbit.annotation.RabbitListener;
import org.springframework.stereotype.Component;

import com.bantads.msconta.common.dto.Evento;
import com.bantads.msconta.common.enums.EEventSource;
import com.bantads.msconta.common.enums.ESaga;
import com.bantads.msconta.common.enums.ESagaStatus;
import com.bantads.msconta.common.enums.ETopics;
import com.bantads.msconta.config.rabbitmq.RabbitMQConstantes;
import com.bantads.msconta.conta.command.service.ContaCommandService;
import com.bantads.msconta.event.producer.ContaEventSagaProducer;
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
    private final ContaEventSagaProducer clienteEventProducer;
    
    @Transactional
    @RabbitListener(queues = RabbitMQConstantes.FILA_CONTA_CMD)
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
            evento.setSource(EEventSource.CONTA_SERVICE);
            evento.setStatus(ESagaStatus.FAIL);
            clienteEventProducer.sendEvent(ETopics.EVT_CONTA_FAIL, evento);
        }

        log.info("Banco de dados de cliente sincronizado com sucesso");
    }

}
