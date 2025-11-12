package com.bantads.msconta.query.consumer;

import org.springframework.amqp.rabbit.annotation.RabbitListener;
import org.springframework.amqp.support.AmqpHeaders;
import org.springframework.messaging.handler.annotation.Header;
import org.springframework.messaging.handler.annotation.Payload;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

import com.bantads.msconta.command.model.Movimentacao;
import com.bantads.msconta.common.conta.dto.ContaSyncEvento;
import com.bantads.msconta.common.conta.enums.TipoMovimentacao;
import com.bantads.msconta.config.rabbitmq.RabbitMQConstantes;
import com.bantads.msconta.query.model.ContaView;
import com.bantads.msconta.query.model.MovimentacaoView;
import com.bantads.msconta.query.repository.ContaViewRepository;
import com.bantads.msconta.query.repository.MovimentacaoViewRepository;

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
    public void handleMovimentacao(@Payload ContaSyncEvento evento, @Header(AmqpHeaders.RECEIVED_ROUTING_KEY) String routingKey){
        log.info("Evento recebido: {}", evento);
        log.info(routingKey);

        if(routingKey.equals("sync.conta.criacao")){
            var contaView = ContaView
                    .builder()
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
        else {
            Movimentacao movOriginal = evento.getMovimentacao();

            if (movimentacaoViewRepository.existsById(movOriginal.getId())) {
                log.warn("Evento duplicado ignorado. ID: {}", movOriginal.getId());
                return;
            }

            ContaView contaViewOrigem = contaViewRepository.findById(evento.getContaIdOrigem())
                    .orElse(new ContaView());

            contaViewOrigem.setId(evento.getContaIdOrigem());
            contaViewOrigem.setSaldo(evento.getNovoSaldoOrigem());
            contaViewRepository.save(contaViewOrigem);

            ContaView contaViewDestino = null;

            if (movOriginal.getTipo().equals(TipoMovimentacao.transferência)) {
                contaViewDestino = contaViewRepository.findById(evento.getContaIdDestino())
                        .orElse(new ContaView());
                contaViewDestino.setId(evento.getContaIdDestino());
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

            log.info("Banco de dados sincronizado com sucesso");
        }
    }
}