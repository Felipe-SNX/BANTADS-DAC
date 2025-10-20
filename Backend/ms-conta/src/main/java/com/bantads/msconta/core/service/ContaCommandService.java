package com.bantads.msconta.core.service;

import com.bantads.msconta.config.rabbitmq.connection.RabbitMQConnection;
import com.bantads.msconta.config.rabbitmq.constantes.RabbitMQConstantes;
import com.bantads.msconta.core.dto.*;
import com.bantads.msconta.core.dto.mapper.ContaMapper;
import com.bantads.msconta.core.enums.TipoMovimentacao;
import com.bantads.msconta.core.exception.ContaNaoEncontradaException;
import com.bantads.msconta.core.model.Conta;
import com.bantads.msconta.core.model.Movimentacao;
import com.bantads.msconta.core.repository.ContaWriteRepository;
import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;

import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.stereotype.Service;
import java.time.LocalDateTime;

@Slf4j
@Service
@AllArgsConstructor
public class ContaCommandService {

    private final ContaWriteRepository contaRepository;
    private final MovimentacaoCommandService movimentacaoService;
    private RabbitTemplate rabbitTemplate;

    public OperacaoResponse depositar(OperacaoRequest operacao, String numConta) {
        Conta conta = contaRepository.findByNumConta(numConta)
                .orElseThrow(() -> new ContaNaoEncontradaException("Conta", numConta));

        conta.depositar(operacao.getValor());

        Conta posDeposito = contaRepository.save(conta);

        var novaMovimentacao = Movimentacao
                .builder()
                .data(LocalDateTime.now())
                .tipo(TipoMovimentacao.DEPOSITO)
                .cpfClienteOrigem(conta.getCpfCliente())
                .cpfClienteDestino(null)
                .valor(operacao.getValor())
                .build();

        movimentacaoService.salvarMovimentacao(novaMovimentacao);

        sendEvent(posDeposito, novaMovimentacao);

        return ContaMapper.toOperacaoResponse(posDeposito);
    }

    public OperacaoResponse sacar(OperacaoRequest operacao, String numConta) {
        Conta conta = contaRepository.findByNumConta(numConta)
                .orElseThrow(() -> new ContaNaoEncontradaException("Conta", numConta));

        conta.sacar(operacao.getValor());

        Conta posSaque = contaRepository.save(conta);

        var novaMovimentacao = Movimentacao
                .builder()
                .data(LocalDateTime.now())
                .tipo(TipoMovimentacao.SAQUE)
                .cpfClienteOrigem(conta.getCpfCliente())
                .cpfClienteDestino(null)
                .valor(operacao.getValor())
                .build();

        movimentacaoService.salvarMovimentacao(novaMovimentacao);

        return ContaMapper.toOperacaoResponse(posSaque);
    }

    public TransferenciaResponse transferir(TransferenciaRequest transferencia, String numConta) {
        Conta contaOrigem = contaRepository.findByNumConta(numConta)
                .orElseThrow(() -> new ContaNaoEncontradaException("Conta", numConta));

        Conta contaDestino = contaRepository.findByNumConta(transferencia.getDestino())
                .orElseThrow(() -> new ContaNaoEncontradaException("Conta", numConta));

        contaOrigem.sacar(transferencia.getValor());
        contaDestino.depositar(transferencia.getValor());

        Conta posSaque = contaRepository.save(contaOrigem);
        contaRepository.save(contaDestino);

        var novaMovimentacao = Movimentacao
                .builder()
                .data(LocalDateTime.now())
                .tipo(TipoMovimentacao.TRANSFERENCIA)
                .cpfClienteOrigem(contaOrigem.getCpfCliente())
                .cpfClienteDestino(contaDestino.getCpfCliente())
                .valor(transferencia.getValor())
                .build();

        movimentacaoService.salvarMovimentacao(novaMovimentacao);

        return TransferenciaResponse
                .builder()
                .numConta(numConta)
                .data(LocalDateTime.now())
                .numContaDestino(contaDestino.getNumConta())
                .saldo(posSaque.getSaldo())
                .valor(transferencia.getValor())
                .build();
    }

    private boolean sendEvent(Conta conta, Movimentacao novaMovimentacao){
        log.info("Publicando evento de movimentação...");
        
        MovimentacaoRealizadaEvent event = new MovimentacaoRealizadaEvent(
            conta.getId(), 
            conta.getSaldo(), 
            novaMovimentacao
        );

        rabbitTemplate.convertAndSend(
            RabbitMQConstantes.NOME_EXCHANGE, 
            RabbitMQConstantes.ROUTING_KEY, 
            event
        );

        return true;
    }
}
