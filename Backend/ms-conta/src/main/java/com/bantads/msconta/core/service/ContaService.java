package com.bantads.msconta.core.service;

import com.bantads.msconta.core.dto.SaldoResponse;
import com.bantads.msconta.core.dto.mapper.ContaMapper;
import com.bantads.msconta.core.exception.ContaNaoEncontradaException;
import com.bantads.msconta.core.model.Conta;
import com.bantads.msconta.core.repository.ContaRepository;
import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;


@Slf4j
@Service
@AllArgsConstructor
public class ContaService {

    private final ContaRepository contaRepository;

    public SaldoResponse consultarSaldo(String numConta) {
        Conta conta = contaRepository.findByNumConta(numConta)
                .orElseThrow(() -> new ContaNaoEncontradaException("Conta", numConta));
        return ContaMapper.toSaldoResponse(conta);
    }

}
