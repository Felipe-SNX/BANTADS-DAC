package com.bantads.msconta.conta.command.service;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.bantads.msconta.conta.command.model.Conta;
import com.bantads.msconta.conta.command.model.Movimentacao;
import com.bantads.msconta.conta.command.repository.ContaWriteRepository;
import com.bantads.msconta.conta.command.repository.MovimentacaoWriteRepository;
import com.bantads.msconta.conta.enums.TipoMovimentacao;
import com.bantads.msconta.conta.query.model.ContaView;
import com.bantads.msconta.conta.query.model.MovimentacaoView;
import com.bantads.msconta.conta.query.repository.ContaViewRepository;
import com.bantads.msconta.conta.query.repository.MovimentacaoViewRepository;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@Slf4j
@Service
@RequiredArgsConstructor
public class DataService {

    private final ContaWriteRepository contaWriteRepository;
    private final ContaViewRepository contaViewRepository;
    private final MovimentacaoWriteRepository movimentacaoWriteRepository;
    private final MovimentacaoViewRepository movimentacaoViewRepository;

    @Transactional 
    public void popularBanco() {
        log.info("Excluindo dados antigo ms-conta");
        contaViewRepository.deleteAll();
        movimentacaoViewRepository.deleteAll();
        contaWriteRepository.deleteAll();
        movimentacaoWriteRepository.deleteAll();

        Conta conta1 = new Conta("1291", new BigDecimal("5000.00"), "12912861012", "98574307084");
        conta1.depositar(new BigDecimal("800.00"));

        Conta conta2 = new Conta("950", new BigDecimal("10000.00"), "09506382000", "64065268052");
        conta2.sacar(new BigDecimal("10000.00")); 

        Conta conta3 = new Conta("8573", new BigDecimal("1500.00"), "85733854057", "23862179060");
        conta3.sacar(new BigDecimal("1000.00")); 

        Conta conta4 = new Conta("5887", new BigDecimal("0.00"), "58872160006", "98574307084");
        conta4.depositar(new BigDecimal("150000.00"));

        Conta conta5 = new Conta("7617", new BigDecimal("0.00"), "76179646090", "64065268052");
        conta5.depositar(new BigDecimal("1500.00"));

        List<Conta> contas = List.of(conta1, conta2, conta3, conta4, conta5);

        contaWriteRepository.saveAll(contas);

        for (Conta conta : contas) {
            var contaView = ContaView
                .builder()
                .id(conta.getId())
                .numConta(conta.getNumConta())
                .dataCriacao(conta.getDataCriacao())
                .saldo(conta.getSaldo())
                .limite(conta.getLimite())
                .cpfCliente(conta.getCpfCliente())
                .cpfGerente(conta.getCpfGerente())
                .build();
                    
            contaViewRepository.save(contaView);
        }
        
        log.info("Carga inicial das contas concluida. " + contas.size() + " contas inseridas.");

        log.info("Criando mapa de CPF para Numero de Conta para enriquecimento da view...");
        Map<String, String> cpfParaNumContaMap = new HashMap<>();
        for (Conta conta : contas) {
            cpfParaNumContaMap.put(conta.getCpfCliente(), conta.getNumConta());
        }

        log.info("Carga inicial das movimentacoes iniciada. ");

        if (movimentacaoWriteRepository.count() == 0) {
            List<Movimentacao> movimentacoes = List.of(
                criarMov("2020-01-01 10:00:00", TipoMovimentacao.DEPOSITO, "12912861012", "1000.00"),
                criarMov("2020-01-01 11:00:00", TipoMovimentacao.DEPOSITO, "12912861012", "900.00"),
                criarMov("2020-01-01 12:00:00", TipoMovimentacao.SAQUE, "12912861012", "550.00"),
                criarMov("2020-01-01 13:00:00", TipoMovimentacao.SAQUE, "12912861012", "350.00"),
                criarMov("2020-01-10 15:00:00", TipoMovimentacao.DEPOSITO, "12912861012", "2000.00"),
                criarMov("2020-01-15 08:00:00", TipoMovimentacao.SAQUE, "12912861012", "500.00"),
                criarMov("2020-01-20 12:00:00", TipoMovimentacao.TRANSFERENCIA, "12912861012", "09506382000", "1700.00"),

                // Movimentações do Cliente 2 (Cleuddônio)
                criarMov("2025-01-01 12:00:00", TipoMovimentacao.DEPOSITO, "09506382000", "1000.00"),
                criarMov("2025-01-02 10:00:00", TipoMovimentacao.DEPOSITO, "09506382000", "5000.00"),
                criarMov("2025-01-10 10:00:00", TipoMovimentacao.SAQUE, "09506382000", "200.00"),
                criarMov("2025-02-05 10:00:00", TipoMovimentacao.DEPOSITO, "09506382000", "7000.00"),

                // Movimentações do Cliente 3 (Catianna)
                criarMov("2025-05-05 00:00:00", TipoMovimentacao.DEPOSITO, "85733854057", "1000.00"),
                criarMov("2025-05-06 00:00:00", TipoMovimentacao.SAQUE, "85733854057", "2000.00"),

                // Movimentações do Cliente 4 (Cutardo)
                criarMov("2025-06-01 00:00:00", TipoMovimentacao.DEPOSITO, "58872160006", "150000.00"),

                // Movimentações do Cliente 5 (Coândrya)
                criarMov("2025-07-01 00:00:00", TipoMovimentacao.DEPOSITO, "76179646090", "1500.00")
            );

            movimentacaoWriteRepository.saveAll(movimentacoes);

            for (Movimentacao mov : movimentacoes) {
                MovimentacaoView view = criarMovView(mov, cpfParaNumContaMap); // Passa o mapa!
                movimentacaoViewRepository.save(view);
            }
        }
        else{
            log.info("Banco de dados de movimentacoes ja populado");
        }

        log.info("Carga de dados de movimentacao realizada");
    }

    private Movimentacao criarMov(String data, TipoMovimentacao tipo, String origem, String valor){
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss");
        
        return Movimentacao
                    .builder()
                    .data(LocalDateTime.parse(data, formatter))
                    .tipo(tipo)
                    .cpfClienteOrigem(origem)
                    .cpfClienteDestino(null)
                    .valor(new BigDecimal(valor))
                    .build();
    }

    private Movimentacao criarMov(String data, TipoMovimentacao tipo, String origem, String destino, String valor){
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss");
        
        return Movimentacao
                    .builder()
                    .data(LocalDateTime.parse(data, formatter))
                    .tipo(tipo)
                    .cpfClienteOrigem(origem)
                    .cpfClienteDestino(destino)
                    .valor(new BigDecimal(valor))
                    .build();
    }

    private MovimentacaoView criarMovView(Movimentacao mov, Map<String, String> cpfParaNumContaMap) {

        String numContaOrigem = cpfParaNumContaMap.get(mov.getCpfClienteOrigem());
        String numContaDestino = null;
        if (mov.getCpfClienteDestino() != null) {
            numContaDestino = cpfParaNumContaMap.get(mov.getCpfClienteDestino());
        }

        return MovimentacaoView
                .builder()
                .id(mov.getId())
                .data(mov.getData())
                .tipo(mov.getTipo())
                .valor(mov.getValor())
                .cpfClienteOrigem(mov.getCpfClienteOrigem())
                .cpfClienteDestino(mov.getCpfClienteDestino())
                .numContaOrigem(numContaOrigem)
                .numContaDestino(numContaDestino)
                .build();
    }
}
