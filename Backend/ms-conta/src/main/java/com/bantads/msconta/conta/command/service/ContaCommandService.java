package com.bantads.msconta.conta.command.service;

import com.bantads.msconta.config.rabbitmq.RabbitMQConstantes;
import com.bantads.msconta.conta.command.model.Conta;
import com.bantads.msconta.conta.command.model.Movimentacao;
import com.bantads.msconta.conta.command.repository.ContaWriteRepository;
import com.bantads.msconta.conta.dto.OperacaoRequest;
import com.bantads.msconta.conta.dto.OperacaoResponse;
import com.bantads.msconta.conta.dto.TransferenciaRequest;
import com.bantads.msconta.conta.dto.TransferenciaResponse;
import com.bantads.msconta.conta.enums.TipoMovimentacao;
import com.bantads.msconta.conta.exception.ContaNaoEncontradaException;
import com.bantads.msconta.conta.exception.TransferenciaInvalidaException;
import com.bantads.msconta.conta.mapper.ContaMapper;
import com.bantads.msconta.event.dto.MovimentacaoRealizadaEvent;

import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;

import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;

@Slf4j
@Service
@AllArgsConstructor
public class ContaCommandService {

    private final ContaWriteRepository contaRepository;
    private final MovimentacaoCommandService movimentacaoService;
    private RabbitTemplate rabbitTemplate;

    @Transactional
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

    @Transactional
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

        sendEvent(posSaque, novaMovimentacao);

        return ContaMapper.toOperacaoResponse(posSaque);
    }

    @Transactional
    public TransferenciaResponse transferir(TransferenciaRequest transferencia, String numConta) {
        Conta contaOrigem = contaRepository.findByNumConta(numConta)
                .orElseThrow(() -> new ContaNaoEncontradaException("Conta", numConta));

        Conta contaDestino = contaRepository.findByNumConta(transferencia.getDestino())
                .orElseThrow(() -> new ContaNaoEncontradaException("Conta", numConta));
        
        if(contaOrigem.getNumConta().equals(contaDestino.getNumConta())){
                throw new TransferenciaInvalidaException("Não é possível transferir para a mesma conta"); 
        }

        contaOrigem.sacar(transferencia.getValor());
        contaDestino.depositar(transferencia.getValor());

        Conta posSaque = contaRepository.save(contaOrigem);
        Conta posDeposito = contaRepository.save(contaDestino);

        var novaMovimentacao = Movimentacao
                .builder()
                .data(LocalDateTime.now())
                .tipo(TipoMovimentacao.TRANSFERENCIA)
                .cpfClienteOrigem(contaOrigem.getCpfCliente())
                .cpfClienteDestino(contaDestino.getCpfCliente())
                .valor(transferencia.getValor())
                .build();

        movimentacaoService.salvarMovimentacao(novaMovimentacao);

        sendEvent(posSaque, novaMovimentacao, posDeposito);

        return TransferenciaResponse
                .builder()
                .numConta(numConta)
                .data(LocalDateTime.now())
                .numContaDestino(contaDestino.getNumConta())
                .saldo(posSaque.getSaldo())
                .valor(transferencia.getValor())
                .build();
    }

    private void sendEvent(Conta conta, Movimentacao novaMovimentacao){
        log.info("Publicando evento de movimentação...");
        
        var event = MovimentacaoRealizadaEvent
                .builder()
                .contaIdOrigem(conta.getId())
                .novoSaldoOrigem(conta.getSaldo())
                .contaIdDestino(null)
                .novoSaldoDestino(null)
                .movimentacao(novaMovimentacao)
                .build();

        rabbitTemplate.convertAndSend(
            RabbitMQConstantes.NOME_EXCHANGE, 
            RabbitMQConstantes.ROUTING_KEY, 
            event
        );
    }

    private void sendEvent(Conta contaOrigem, Movimentacao novaMovimentacao, Conta contaDestino){
        log.info("Publicando evento de movimentação...");
        
        var event = MovimentacaoRealizadaEvent
                .builder()
                .contaIdOrigem(contaOrigem.getId())
                .novoSaldoOrigem(contaOrigem.getSaldo())
                .contaIdDestino(contaDestino.getId())
                .novoSaldoDestino(contaDestino.getSaldo())
                .movimentacao(novaMovimentacao)
                .build();

        rabbitTemplate.convertAndSend(
            RabbitMQConstantes.NOME_EXCHANGE, 
            RabbitMQConstantes.ROUTING_KEY, 
            event
        );
    }
}
