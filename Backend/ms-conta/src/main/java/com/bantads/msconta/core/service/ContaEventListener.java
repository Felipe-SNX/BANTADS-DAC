package com.bantads.msconta.core.service;

import org.springframework.amqp.rabbit.annotation.RabbitListener;
import org.springframework.messaging.handler.annotation.Payload;
import org.springframework.stereotype.Component;

import com.bantads.msconta.config.rabbitmq.constantes.RabbitMQConstantes;
import com.bantads.msconta.core.dto.MovimentacaoRealizadaEvent;
import com.bantads.msconta.core.enums.TipoMovimentacao;
import com.bantads.msconta.core.model.ContaView;
import com.bantads.msconta.core.model.Movimentacao;
import com.bantads.msconta.core.model.MovimentacaoView;
import com.bantads.msconta.core.repository.ContaViewRepository;
import com.bantads.msconta.core.repository.MovimentacaoViewRepository;

import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@Slf4j
@Component
@AllArgsConstructor
public class ContaEventListener {

    private final ContaViewRepository contaViewRepository;
    private final MovimentacaoViewRepository movimentacaoViewRepository;

    @RabbitListener(queues = RabbitMQConstantes.FILA_CONTA_SYNC)
    public void handleMovimentacao(@Payload MovimentacaoRealizadaEvent event){
        log.info("Evento recebido: {}", event);

        Movimentacao movOriginal = event.getMovimentacao();

        MovimentacaoView movView = MovimentacaoView.builder()
                .id(movOriginal.getId()) 
                .data(movOriginal.getData())
                .tipo(movOriginal.getTipo())
                .cpfClienteOrigem(movOriginal.getCpfClienteOrigem())
                .cpfClienteDestino(movOriginal.getCpfClienteDestino())
                .valor(movOriginal.getValor())
                .build();
        
        movimentacaoViewRepository.save(movView);

        ContaView contaView = contaViewRepository.findById(event.getContaIdOrigem())
                .orElse(new ContaView());
        
        contaView.setId(event.getContaIdOrigem()); 
        contaView.setSaldo(event.getNovoSaldoOrigem());

        contaViewRepository.save(contaView);

        if(movOriginal.getTipo().equals(TipoMovimentacao.TRANSFERENCIA)){
            ContaView contaViewDestino = contaViewRepository.findById(event.getContaIdDestino())
                    .orElse(new ContaView());
            contaViewDestino.setId(event.getContaIdDestino()); 
            contaViewDestino.setSaldo(event.getNovoSaldoDestino());

            contaViewRepository.save(contaViewDestino);
        }

        log.info("Banco de dados sincronizado com sucesso");
    }

}
