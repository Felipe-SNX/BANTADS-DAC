package com.bantads.msconta.query.consumer;

import org.springframework.amqp.core.Message;
import org.springframework.amqp.rabbit.annotation.RabbitListener;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

import com.bantads.msconta.command.model.Movimentacao;
import com.bantads.msconta.common.conta.dto.ContaMovSyncEvento;
import com.bantads.msconta.common.conta.dto.ContaSyncEvento;
import com.bantads.msconta.common.conta.enums.TipoMovimentacao;
import com.bantads.msconta.config.rabbitmq.RabbitMQConstantes;
import com.bantads.msconta.query.model.ContaView;
import com.bantads.msconta.query.model.MovimentacaoView;
import com.bantads.msconta.query.repository.ContaViewRepository;
import com.bantads.msconta.query.repository.MovimentacaoViewRepository;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;

import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@Slf4j
@Component
@AllArgsConstructor
public class ContaCQRSEventConsumer {

    private final ContaViewRepository contaViewRepository;
    private final MovimentacaoViewRepository movimentacaoViewRepository;
    private final ObjectMapper objectMapper;

    @Transactional
    @RabbitListener(queues = RabbitMQConstantes.FILA_CONTA_SYNC)
    public void handleSyncEvents(Message message) {
        String routingKey = message.getMessageProperties().getReceivedRoutingKey();
        String payload = new String(message.getBody(), java.nio.charset.StandardCharsets.UTF_8);

        log.info("Evento CQRS Recebido. Routing Key: {}", routingKey);

        try {
            switch (routingKey) {
                case "sync.conta.criada":
                    ContaSyncEvento dtoCriacao = objectMapper.readValue(payload, ContaSyncEvento.class);
                    handleContaCriada(dtoCriacao);
                    break;
                    
                case "sync.conta.atualizada":
                    ContaSyncEvento dtoAtualizacao = objectMapper.readValue(payload, ContaSyncEvento.class);
                    handleContaAtualizada(dtoAtualizacao);
                    break;
                    
                case "sync.conta.deletada":
                    String cpfCliente = objectMapper.readValue(payload, String.class);
                    handleContaDeletada(cpfCliente);
                    break;

                case "sync.conta.movimentacao":
                    ContaMovSyncEvento dtoMov = objectMapper.readValue(payload, ContaMovSyncEvento.class);
                    handleMovimentacao(dtoMov);
                    break;
                    
                default:
                    log.warn("Evento CQRS com routing key desconhecida recebido: {}", routingKey);
                    break;
            }
        } catch (JsonProcessingException e) {
            log.error("Falha ao desserializar payload para a routing key {}: {}", routingKey, e.getMessage());
        } catch (Exception e) {
            log.error("Erro inesperado ao processar evento CQRS {}: {}", routingKey, e.getMessage(), e);
        }
    }

  
    private void handleContaCriada(ContaSyncEvento evento) {
        var contaView = ContaView.builder()
                .id(evento.getId()) 
                .conta(evento.getConta())
                .dataCriacao(evento.getDataCriacao())
                .cliente(evento.getCliente())
                .gerente(evento.getGerente())
                .saldo(evento.getSaldo())
                .limite(evento.getLimite())
                .build();
        
        contaViewRepository.save(contaView);
    }
    

    private void handleContaAtualizada(ContaSyncEvento evento) {
        log.info("Processando: sync.conta.atualizada para conta {}", evento.getConta());
        
        ContaView contaView = contaViewRepository.findById(evento.getId())
                .orElse(new ContaView()); 

        contaView.setId(evento.getId());
        contaView.setConta(evento.getConta());
        contaView.setDataCriacao(evento.getDataCriacao()); 
        contaView.setCliente(evento.getCliente());
        contaView.setGerente(evento.getGerente());
        contaView.setSaldo(evento.getSaldo());
        contaView.setLimite(evento.getLimite());

        contaViewRepository.save(contaView);
    }
    
    private void handleContaDeletada(String cpfCliente) {
        log.info("Processando: sync.conta.deletada para cliente {}", cpfCliente);
        
        contaViewRepository.deleteByCliente(cpfCliente);
    }

    private void handleMovimentacao(ContaMovSyncEvento evento) {
        log.info("Processando: sync.conta.movimentacao");
        Movimentacao movOriginal = evento.getMovimentacao();

        if (movimentacaoViewRepository.existsById(movOriginal.getId())) {
            log.warn("Evento duplicado ignorado. ID: {}", movOriginal.getId());
            return;
        }

        ContaView contaViewOrigem = contaViewRepository.findById(evento.getContaIdOrigem())
                .orElseThrow(() -> new RuntimeException("Conta de origem não encontrada no banco de leitura: " + evento.getContaIdOrigem()));

        contaViewOrigem.setSaldo(evento.getNovoSaldoOrigem());
        contaViewRepository.save(contaViewOrigem);

        ContaView contaViewDestino = null;

        if (movOriginal.getTipo().equals(TipoMovimentacao.transferência)) {
            contaViewDestino = contaViewRepository.findById(evento.getContaIdDestino())
                    .orElseThrow(() -> new RuntimeException("Conta de destino não encontrada no banco de leitura: " + evento.getContaIdDestino()));
            
            contaViewDestino.setSaldo(evento.getNovoSaldoDestino());
            contaViewRepository.save(contaViewDestino);
        }

        MovimentacaoView movView = MovimentacaoView.builder()
                .id(movOriginal.getId())
                .data(movOriginal.getData())
                .tipo(movOriginal.getTipo())
                .cpfClienteOrigem(movOriginal.getCpfClienteOrigem())
                .cpfClienteDestino(movOriginal.getCpfClienteDestino())
                .origem(contaViewOrigem.getConta())
                .destino(contaViewDestino != null ? contaViewDestino.getConta() : null)
                .valor(movOriginal.getValor())
                .build();

        movimentacaoViewRepository.save(movView);
    }
}