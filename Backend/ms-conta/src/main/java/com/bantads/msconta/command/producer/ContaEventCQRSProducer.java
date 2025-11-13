package com.bantads.msconta.command.producer;

import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.stereotype.Component;

import com.bantads.msconta.command.model.Conta;
import com.bantads.msconta.command.model.Movimentacao;
import com.bantads.msconta.common.conta.dto.ContaMovSyncEvento;
import com.bantads.msconta.common.conta.dto.ContaSyncEvento;
import com.bantads.msconta.config.rabbitmq.RabbitMQConstantes;
import com.bantads.msconta.common.conta.mapper.ContaMapper; 

import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@Slf4j
@Component
@AllArgsConstructor
public class ContaEventCQRSProducer {

    private RabbitTemplate rabbitTemplate;
    
    public void publicarContaCriada(Conta conta) {
        log.info("Publicando evento CQRS: sync.conta.criada");
        ContaSyncEvento eventoDTO = ContaMapper.toContaSyncEvento(conta); 

        rabbitTemplate.convertAndSend(
                RabbitMQConstantes.NOME_EXCHANGE,
                "sync.conta.criada", 
                eventoDTO
        );
    }

    public void publicarContaAtualizada(Conta conta) {
        log.info("Publicando evento CQRS: sync.conta.atualizada");
        ContaSyncEvento eventoDTO = ContaMapper.toContaSyncEvento(conta); 

        rabbitTemplate.convertAndSend(
                RabbitMQConstantes.NOME_EXCHANGE,
                "sync.conta.atualizada", 
                eventoDTO
        );
    }
    
    public void publicarContaDeletada(Conta conta) {
        log.info("Publicando evento CQRS: sync.conta.deletada");
        
        String clienteCpf = conta.getCliente(); 

        rabbitTemplate.convertAndSend(
                RabbitMQConstantes.NOME_EXCHANGE,
                "sync.conta.deletada", 
                clienteCpf 
        );
    }

    public void publicarMovimentacao(Conta conta, Movimentacao novaMovimentacao) {
        log.info("Publicando evento CQRS: sync.conta.movimentacao");
        
        var event = ContaMovSyncEvento.builder()
                .contaIdOrigem(conta.getId())
                .novoSaldoOrigem(conta.getSaldo())
                .movimentacao(novaMovimentacao)
                .build();

        rabbitTemplate.convertAndSend(
            RabbitMQConstantes.NOME_EXCHANGE, 
            "sync.conta.movimentacao",
            event
        );
    }

    public void publicarMovimentacao(Conta contaOrigem, Movimentacao novaMovimentacao, Conta contaDestino) {
        log.info("Publicando evento CQRS: sync.conta.movimentacao (transferência)");
        
        var event = ContaMovSyncEvento.builder()
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