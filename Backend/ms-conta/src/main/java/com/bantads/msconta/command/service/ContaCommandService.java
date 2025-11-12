package com.bantads.msconta.command.service;

import com.bantads.msconta.command.model.Conta;
import com.bantads.msconta.command.model.Movimentacao;
import com.bantads.msconta.command.producer.ContaEventCQRSProducer;
import com.bantads.msconta.command.repository.ContaWriteRepository;
import com.bantads.msconta.common.conta.dto.DadosClienteConta;
import com.bantads.msconta.common.conta.dto.GerentesNumeroContasDto;
import com.bantads.msconta.common.conta.dto.OperacaoRequest;
import com.bantads.msconta.common.conta.dto.OperacaoResponse;
import com.bantads.msconta.common.conta.dto.PerfilInfo;
import com.bantads.msconta.common.conta.dto.TransferenciaRequest;
import com.bantads.msconta.common.conta.dto.TransferenciaResponse;
import com.bantads.msconta.common.conta.enums.TipoMovimentacao;
import com.bantads.msconta.common.conta.exception.ContaNaoEncontradaException;
import com.bantads.msconta.common.conta.exception.TransferenciaInvalidaException;
import com.bantads.msconta.common.conta.mapper.ContaMapper;
import com.bantads.msconta.common.saga.dto.DadoGerenteInsercao;

import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.security.SecureRandom;
import java.time.LocalDateTime;
import java.time.temporal.ChronoUnit;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

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
        Conta conta = contaRepository.findByConta(numConta)
                .orElseThrow(() -> new ContaNaoEncontradaException("Conta", numConta));

        conta.depositar(operacao.getValor());

        Conta posDeposito = contaRepository.save(conta);

        var novaMovimentacao = Movimentacao
                .builder()
                .data(LocalDateTime.now().truncatedTo(ChronoUnit.MICROS)) //O Uso de ChronoUnit é pq dava erro
                .tipo(TipoMovimentacao.depósito)                          //no teste por precisão de casas
                .cpfClienteOrigem(conta.getCliente())                     //decimais na dataCriacao
                .cpfClienteDestino(null)
                .valor(operacao.getValor())
                .build();

        Movimentacao movimentacao = movimentacaoService.salvarMovimentacao(novaMovimentacao);

        eventProducer.publicarMovimentacao(posDeposito, movimentacao);

        OperacaoResponse operacaoResponse = ContaMapper.toOperacaoResponse(posDeposito);
        operacaoResponse.setData(movimentacao.getData());

        return operacaoResponse;
    }

    @Transactional
    public OperacaoResponse sacar(OperacaoRequest operacao, String numConta) {
        Conta conta = contaRepository.findByConta(numConta)
                .orElseThrow(() -> new ContaNaoEncontradaException("Conta", numConta));

        conta.sacar(operacao.getValor());

        Conta posSaque = contaRepository.save(conta);

        var novaMovimentacao = Movimentacao
                .builder()
                .data(LocalDateTime.now().truncatedTo(ChronoUnit.MICROS))
                .tipo(TipoMovimentacao.saque)
                .cpfClienteOrigem(conta.getCliente())
                .cpfClienteDestino(null)
                .valor(operacao.getValor())
                .build();

        Movimentacao movimentacao = movimentacaoService.salvarMovimentacao(novaMovimentacao);

        eventProducer.publicarMovimentacao(posSaque, movimentacao);

        OperacaoResponse operacaoResponse = ContaMapper.toOperacaoResponse(posSaque);
        operacaoResponse.setData(movimentacao.getData());

        return operacaoResponse;
    }

    @Transactional
    public TransferenciaResponse transferir(TransferenciaRequest transferencia, String numConta) {
        Conta contaOrigem = contaRepository.findByConta(numConta)
                .orElseThrow(() -> new ContaNaoEncontradaException("Conta", numConta));

        Conta contaDestino = contaRepository.findByConta(transferencia.getDestino())
                .orElseThrow(() -> new ContaNaoEncontradaException("Conta", numConta));
        
        if(contaOrigem.getConta().equals(contaDestino.getConta())){
                throw new TransferenciaInvalidaException("Não é possível transferir para a mesma conta"); 
        }

        contaOrigem.sacar(transferencia.getValor());
        contaDestino.depositar(transferencia.getValor());

        Conta posSaque = contaRepository.save(contaOrigem);
        Conta posDeposito = contaRepository.save(contaDestino);

        var novaMovimentacao = Movimentacao
                .builder()
                .data(LocalDateTime.now().truncatedTo(ChronoUnit.MICROS))
                .tipo(TipoMovimentacao.transferência)
                .cpfClienteOrigem(contaOrigem.getCliente())
                .cpfClienteDestino(contaDestino.getCliente())
                .valor(transferencia.getValor())
                .build();

        Movimentacao movimentacao = movimentacaoService.salvarMovimentacao(novaMovimentacao);

        eventProducer.publicarMovimentacao(posSaque, movimentacao, posDeposito);

        return TransferenciaResponse
                .builder()
                .conta(numConta)
                .data(movimentacao.getData())
                .destino(contaDestino.getConta())
                .saldo(posSaque.getSaldo())
                .valor(transferencia.getValor())
                .build();
    }

    @Transactional
    public Conta atribuirContas(DadoGerenteInsercao dadoGerenteInsercao) {
        String cpfComMaisContas = getGerenteComMaisContas();
        Optional<Conta> contaOpt = contaRepository.findFirstByGerenteOrderByDataCriacaoAsc(cpfComMaisContas);

        if (contaOpt.isEmpty()) {
            log.warn("Nenhuma conta encontrada para remanejar para o novo gerente {}", dadoGerenteInsercao.getCpf());
            return null; 
        }
        
        Conta contaReal = contaOpt.get();

        Conta contaAntiga = new Conta();
        contaAntiga.setCliente(contaReal.getCliente());
        contaAntiga.setGerente(contaReal.getGerente());

        contaReal.setGerente(dadoGerenteInsercao.getCpf());
        Conta contaAtualizada = contaRepository.save(contaReal);
        
        eventProducer.publicarContaAtualizada(contaAtualizada);

        return contaAntiga;
    }

    @Transactional
    public Conta criarConta(DadosClienteConta dadosClienteConta){

        var conta = Conta
                .builder()
                .conta(gerarNumConta())
                .dataCriacao(LocalDateTime.now())
                .saldo(BigDecimal.valueOf(0))
                .limite(calcularLimite(dadosClienteConta.getSalario()))
                .cliente(dadosClienteConta.getCliente())
                .gerente(dadosClienteConta.getGerente())
                .build();
        
        Conta contaCriada = contaRepository.save(conta);

        eventProducer.publicarContaCriada(contaCriada);

        return contaCriada;
    }

    @Transactional
    public BigDecimal atualizarLimite(PerfilInfo perfilInfo, String cpf){
        Conta conta = buscarContaPorCpfCliente(cpf);

        BigDecimal limiteAntigo = conta.getLimite();

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

        Conta contaAtualizada = contaRepository.save(conta);

        eventProducer.publicarContaAtualizada(contaAtualizada);

        return limiteAntigo;
    }

    @Transactional 
    public void reverterAlteracaoLimite(String cpf, BigDecimal limiteAntigo) {
        Conta conta = buscarContaPorCpfCliente(cpf);
        conta.setLimite(limiteAntigo);
        Conta contaRevertida = contaRepository.save(conta); 
        
        eventProducer.publicarContaAtualizada(contaRevertida);
    }

    @Transactional
    public void reverterRemanejamento(String cpfGerenteRestaurado, List<String> cpfsClientesAfetados) {
        List<Conta> contasParaReverter = contaRepository.findAllByClienteIn(cpfsClientesAfetados);

        for (Conta conta : contasParaReverter) {
            conta.setGerente(cpfGerenteRestaurado);
            Conta contaRevertida = contaRepository.save(conta);
            
            eventProducer.publicarContaAtualizada(contaRevertida);
        }
    }

    @Transactional 
    public void reverterAlteracaoGerente(Conta contaAntiga) {
        Conta contaAtual = buscarContaPorCpfCliente(contaAntiga.getCliente());
        contaAtual.setGerente(contaAntiga.getGerente());
        Conta contaRevertida = contaRepository.save(contaAtual);
        eventProducer.publicarContaAtualizada(contaRevertida);
    }

    @Transactional 
    public void excluirConta(String cpf) {
        Optional<Conta> contaOpt = contaRepository.findByCliente(cpf);
        
        if (contaOpt.isPresent()) {
            Conta contaParaExcluir = contaOpt.get();
            eventProducer.publicarContaDeletada(contaParaExcluir); 
            contaRepository.delete(contaParaExcluir);
        } else {
            log.warn("Compensação de 'excluirConta' não encontrou conta para o CPF {}", cpf);
        }
    }

    @Transactional 
    public List<String> remanejarGerentes(String cpf) {
        List<Conta> contas = contaRepository.findAllByGerente(cpf);

        List<String> cpfsClientesAfetados = contas.stream()
                .map(Conta::getCliente)
                .collect(Collectors.toList());

        for (Conta conta : contas) {
            String cpfNovoGerente = buscarCpfGerenteComMenosContasRemanejar(cpf);
            conta.setGerente(cpfNovoGerente);
            Conta contaAtualizada = contaRepository.save(conta);
            
            eventProducer.publicarContaAtualizada(contaAtualizada);
        }

        return cpfsClientesAfetados;
    }

    public List<GerentesNumeroContasDto> buscarNumeroDeContasPorGerente(){
        return contaRepository.countContasByGerente();
    }

    private String getGerenteComMaisContas() {
        Pageable topUm = PageRequest.of(0, 1);

        Page<String> resultado = contaRepository.findGerentesOrdenadosPorContasEData(topUm);

        if (!resultado.hasContent()) {
            throw new RuntimeException("Nenhum gerente encontrado.");
        }

        return resultado.getContent().get(0);
    }

    private Conta buscarContaPorCpfCliente(String cpf){
        return contaRepository.findByCliente(cpf)
                .orElseThrow(() -> new ContaNaoEncontradaException("Conta", cpf));
    }

    private String buscarCpfGerenteComMenosContasRemanejar(String cpf){
        return contaRepository.findGerenteComMenosContasRemanejar(cpf);
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
