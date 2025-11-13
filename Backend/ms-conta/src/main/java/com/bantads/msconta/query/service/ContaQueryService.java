package com.bantads.msconta.query.service;

import java.util.ArrayList;
import java.util.List;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.bantads.msconta.common.conta.dto.DadoConta;
import com.bantads.msconta.common.conta.dto.ExtratoResponse;
import com.bantads.msconta.common.conta.dto.ItemExtratoResponse;
import com.bantads.msconta.common.conta.dto.SaldoResponse;
import com.bantads.msconta.common.conta.exception.ContaNaoEncontradaException;
import com.bantads.msconta.common.conta.mapper.ContaViewMapper;
import com.bantads.msconta.query.model.ContaView;
import com.bantads.msconta.query.repository.ContaViewRepository;

import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@Slf4j
@Service
@AllArgsConstructor
public class ContaQueryService {

    private final ContaViewRepository contaRepository;
    private final MovimentacaoQueryService movimentacaoService;

    @Transactional(readOnly = true)
    public SaldoResponse consultarSaldo(String numConta) {
        ContaView conta = contaRepository.findByConta(numConta)
                .orElseThrow(() -> new ContaNaoEncontradaException("Conta", numConta));
        return ContaViewMapper.toSaldoResponse(conta);
    }

    @Transactional(readOnly = true)
    public DadoConta getContaByClienteCpf(String cpf) {
        ContaView conta = contaRepository.findByCliente(cpf)
                .orElseThrow(() -> new ContaNaoEncontradaException("Conta", cpf));
        return ContaViewMapper.toDadoConta(conta);
    }

    @Transactional(readOnly = true)
    public ExtratoResponse extrato(String numConta) {
        ContaView conta = contaRepository.findByConta(numConta)
                .orElseThrow(() -> new ContaNaoEncontradaException("Conta", numConta));

        List<ItemExtratoResponse> movimentacoes = movimentacaoService.buscarMovimentacoesPorCpf(conta.getCliente());

        return ExtratoResponse
                .builder()
                .conta(numConta)
                .saldo(conta.getSaldo())
                .movimentacoes(movimentacoes)
                .build();
    }

    @Transactional(readOnly = true)
    public List<DadoConta> getAllDadosConta(){
        List<ContaView> contas = contaRepository.findAll();

        List<DadoConta> dadoContas  = new ArrayList<>();
        for (ContaView conta : contas) {
            dadoContas.add(ContaViewMapper.toDadoConta(conta));
        }

        return dadoContas;
    }
}
