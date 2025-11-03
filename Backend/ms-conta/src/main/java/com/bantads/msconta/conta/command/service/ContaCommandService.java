package com.bantads.msconta.conta.command.service;

import com.bantads.msconta.common.dto.DadoGerenteInsercao;
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
import com.bantads.msconta.event.dto.AutoCadastroInfo;
import com.bantads.msconta.event.dto.PerfilInfo;
import com.bantads.msconta.event.producer.ContaEventCQRSProducer;

import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.security.SecureRandom;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

@Slf4j
@Service
@AllArgsConstructor
public class ContaCommandService {

    private final ContaWriteRepository contaRepository;
    private final MovimentacaoCommandService movimentacaoService;
    private final ContaEventCQRSProducer eventProducer;
    private final SecureRandom random;


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

        eventProducer.sendSyncReadDatabaseEvent(posDeposito, novaMovimentacao);

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

        eventProducer.sendSyncReadDatabaseEvent(posSaque, novaMovimentacao);

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

        eventProducer.sendSyncReadDatabaseEvent(posSaque, novaMovimentacao, posDeposito);

        return TransferenciaResponse
                .builder()
                .numConta(numConta)
                .data(LocalDateTime.now())
                .numContaDestino(contaDestino.getNumConta())
                .saldo(posSaque.getSaldo())
                .valor(transferencia.getValor())
                .build();
    }

    @Transactional
    public void atribuirContas(DadoGerenteInsercao dadoGerenteInsercao){
        String cpfComMaisContas = contaRepository.findCpfGerenteComMaisContas();
        Optional<Conta> contaEscolhida = contaRepository.findFirstByCpfGerenteOrderByDataCriacaoAsc(cpfComMaisContas);

        if(contaEscolhida.isPresent()){
            contaEscolhida.get().setCpfGerente(dadoGerenteInsercao.getCpf());
            contaRepository.save(contaEscolhida.get());
        }
    }

    public void criarConta(AutoCadastroInfo autoCadastroInfo){
        String cpfGerente = buscarCpfGerenteComMenosContas();
        
        var conta = Conta
                .builder()
                .numConta(gerarNumConta())
                .dataCriacao(LocalDateTime.now())
                .saldo(BigDecimal.valueOf(0))
                .limite(calcularLimite(autoCadastroInfo.getSalario()))
                .cpfCliente(autoCadastroInfo.getCpf())
                .cpfGerente(cpfGerente)
                .ativo(false)
                .build();
        
        contaRepository.save(conta);
    }

    public void atualizarLimite(PerfilInfo perfilInfo, String cpf){
        Conta conta = buscarContaPorCpfCliente(cpf);

        BigDecimal novoLimite = calcularLimite(perfilInfo.getSalario());
        BigDecimal saldoAtual = conta.getSaldo();

        //Se o saldo for negativo ao somar com limite deve dar um valor positivo, caso isso não aconteça o novo
        //limite será o valor atual do saldo multiplicado por -1, ou seja, o valor positivo
        BigDecimal zero = BigDecimal.ZERO;
        BigDecimal resultado = saldoAtual.add(novoLimite); 
        if (resultado.compareTo(zero) < 0) {
                conta.setLimite(saldoAtual.multiply(BigDecimal.valueOf(-1)));  
        }       
        else{
                conta.setLimite(novoLimite);
        }

        contaRepository.save(conta);
    }

    public void remanejarGerentes(String cpf){
        List<Conta> contas = contaRepository.findAllByCpfGerente(cpf);

        if(contas.isEmpty()){
                return;
        }

        for (Conta conta : contas) {
                String cpfNovoGerente = buscarCpfGerenteComMenosContasRemanejar(cpf);
                conta.setCpfGerente(cpfNovoGerente);
                contaRepository.save(conta);
        }

    }

    private Conta buscarContaPorCpfCliente(String cpf){
        return contaRepository.findByCpfCliente(cpf)
                .orElseThrow(() -> new ContaNaoEncontradaException("Conta", cpf));
    }

    private String buscarCpfGerenteComMenosContas(){
        return contaRepository.findCpfGerenteComMenosContas();
    }

    private String buscarCpfGerenteComMenosContasRemanejar(String cpf){
        return contaRepository.findCpfGerenteComMenosContasRemanejar(cpf);
    }

    private String gerarNumConta(){
        int numero = random.nextInt(10000); 
        return String.format("%04d", numero);
    }

    private BigDecimal calcularLimite(BigDecimal salario){
        BigDecimal divisor = BigDecimal.valueOf(2);
        int escala = 2; //Número de casas decimais
        RoundingMode modo = RoundingMode.HALF_UP; 
        return salario.divide(divisor, escala, modo);
    }
}
