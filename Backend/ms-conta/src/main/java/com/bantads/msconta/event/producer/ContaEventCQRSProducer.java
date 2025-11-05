package com.bantads.msconta.event.producer;

import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.stereotype.Component;

import com.bantads.msconta.config.rabbitmq.RabbitMQConstantes;
import com.bantads.msconta.conta.command.model.Conta;
import com.bantads.msconta.conta.command.model.Movimentacao;
import com.bantads.msconta.event.dto.ContaSyncEvento;

import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@Slf4j
@Component
@AllArgsConstructor
public class ContaEventCQRSProducer {

    private RabbitTemplate rabbitTemplate;

    public void sendSyncReadDatabaseEvent(Conta conta){
        log.info("Publicando evento de movimentação...");

        rabbitTemplate.convertAndSend(
                RabbitMQConstantes.NOME_EXCHANGE,
                "sync.conta.criacao",
                conta
        );
    }

    public void sendSyncReadDatabaseEvent(Conta conta, Movimentacao novaMovimentacao){
        log.info("Publicando evento de movimentação...");
        
        var event = ContaSyncEvento
                .builder()
                .contaIdOrigem(conta.getId())
                .novoSaldoOrigem(conta.getSaldo())
                .contaIdDestino(null)
                .novoSaldoDestino(null)
                .movimentacao(novaMovimentacao)
                .build();

        rabbitTemplate.convertAndSend(
            RabbitMQConstantes.NOME_EXCHANGE, 
            "sync.conta.movimentacao",
            event
        );
    }

    public void sendSyncReadDatabaseEvent(Conta contaOrigem, Movimentacao novaMovimentacao, Conta contaDestino){
        log.info("Publicando evento de movimentação...");
        
        var event = ContaSyncEvento
                .builder()
                .contaIdOrigem(contaOrigem.getId())
                .novoSaldoOrigem(contaOrigem.getSaldo())
                .contaIdDestino(contaDestino.getId())
                .novoSaldoDestino(contaDestino.getSaldo())
                .movimentacao(novaMovimentacao)
                .build();

        rabbitTemplate.convertAndSend(
            RabbitMQConstantes.NOME_EXCHANGE,
            "sync.conta.movimentacao",
            event
        );
    }
}
