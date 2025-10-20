package com.bantads.msconta.core.service;

import java.util.List;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.bantads.msconta.core.dto.ExtratoResponse;
import com.bantads.msconta.core.dto.ItemExtratoResponse;
import com.bantads.msconta.core.dto.SaldoResponse;
import com.bantads.msconta.core.dto.mapper.ContaMapper;
import com.bantads.msconta.core.exception.ContaNaoEncontradaException;
import com.bantads.msconta.core.model.Conta;
import com.bantads.msconta.core.repository.ContaViewRepository;
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
        Conta conta = contaRepository.findByNumConta(numConta)
                .orElseThrow(() -> new ContaNaoEncontradaException("Conta", numConta));
        return ContaMapper.toSaldoResponse(conta);
    }

    @Transactional(readOnly = true)
    public ExtratoResponse extrato(String numConta) {
        Conta conta = contaRepository.findByNumConta(numConta)
                .orElseThrow(() -> new ContaNaoEncontradaException("Conta", numConta));

        List<ItemExtratoResponse> movimentacoes = movimentacaoService.buscarMovimentacoesPorNumConta(conta.getCpfCliente());

        return ExtratoResponse
                .builder()
                .numConta(numConta)
                .saldo(conta.getSaldo())
                .movimentacoes(movimentacoes)
                .build();
    }
}
