package com.bantads.mscliente.core.consumer;

import org.springframework.amqp.rabbit.annotation.RabbitListener;
import org.springframework.stereotype.Component;
import com.bantads.mscliente.config.rabbitmq.RabbitMQConstantes;
import com.bantads.mscliente.core.dto.Evento;
import com.bantads.mscliente.core.enums.ESaga;
import com.bantads.mscliente.core.service.ClienteService;
import jakarta.transaction.Transactional;
import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@Slf4j
@Component
@AllArgsConstructor
public class ClienteEventConsumer {

    private final ClienteService clienteService;
    
    @Transactional
    @RabbitListener(queues = RabbitMQConstantes.FILA_CLIENTES)
    public void handleAlteracoes(Evento event){
        log.info("Evento recebido: {}", event);

        ESaga sagaType = event.getSaga();

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

        }

        log.info("Banco de dados de cliente sincronizado com sucesso");
    }
}
