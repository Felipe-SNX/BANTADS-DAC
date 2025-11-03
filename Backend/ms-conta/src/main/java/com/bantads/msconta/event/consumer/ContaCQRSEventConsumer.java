package com.bantads.msconta.event.consumer;

import org.springframework.amqp.rabbit.annotation.RabbitListener;
import org.springframework.messaging.handler.annotation.Payload;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

import com.bantads.msconta.config.rabbitmq.RabbitMQConstantes;
import com.bantads.msconta.conta.command.model.Movimentacao;
import com.bantads.msconta.conta.enums.TipoMovimentacao;
import com.bantads.msconta.conta.query.model.ContaView;
import com.bantads.msconta.conta.query.model.MovimentacaoView;
import com.bantads.msconta.conta.query.repository.ContaViewRepository;
import com.bantads.msconta.conta.query.repository.MovimentacaoViewRepository;
import com.bantads.msconta.event.dto.MovimentacaoRealizadaEvent;

import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@Slf4j
@Component
@AllArgsConstructor
public class ContaCQRSEventConsumer {

    private final ContaViewRepository contaViewRepository;
    private final MovimentacaoViewRepository movimentacaoViewRepository;

    @Transactional
    @RabbitListener(queues = RabbitMQConstantes.FILA_CONTA_SYNC)
    public void handleMovimentacao(@Payload MovimentacaoRealizadaEvent event){
        log.info("Evento recebido: {}", event);

        Movimentacao movOriginal = event.getMovimentacao();

        if (movimentacaoViewRepository.existsById(movOriginal.getId())) {
            log.warn("Evento duplicado ignorado. ID: {}", movOriginal.getId());
            return; 
        }

        ContaView contaViewOrigem = contaViewRepository.findById(event.getContaIdOrigem())
                .orElse(new ContaView()); 

        contaViewOrigem.setId(event.getContaIdOrigem());
        contaViewOrigem.setSaldo(event.getNovoSaldoOrigem());
        contaViewRepository.save(contaViewOrigem);

        ContaView contaViewDestino = null; 

        if(movOriginal.getTipo().equals(TipoMovimentacao.TRANSFERENCIA)){
            contaViewDestino = contaViewRepository.findById(event.getContaIdDestino())
                    .orElse(new ContaView()); 
            contaViewDestino.setId(event.getContaIdDestino());
            contaViewDestino.setSaldo(event.getNovoSaldoDestino());
            
            contaViewRepository.save(contaViewDestino);
        }

        MovimentacaoView movView = MovimentacaoView.builder()
                .id(movOriginal.getId())
                .data(movOriginal.getData())
                .tipo(movOriginal.getTipo())
                .cpfClienteOrigem(movOriginal.getCpfClienteOrigem())
                .cpfClienteDestino(movOriginal.getCpfClienteDestino())
                .numContaOrigem(contaViewOrigem.getNumConta())
                .numContaDestino(contaViewDestino != null ? contaViewDestino.getNumConta() : null) 
                .valor(movOriginal.getValor())
                .build();

        movimentacaoViewRepository.save(movView);

        log.info("Banco de dados sincronizado com sucesso");
    }
}