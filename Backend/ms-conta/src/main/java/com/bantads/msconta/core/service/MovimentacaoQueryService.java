package com.bantads.msconta.core.service;

import java.util.List;

import org.springframework.stereotype.Service;

import com.bantads.msconta.core.dto.ItemExtratoResponse;
import com.bantads.msconta.core.repository.MovimentacaoViewRepository;
import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@Slf4j
@Service
@AllArgsConstructor
public class MovimentacaoQueryService {

    private final MovimentacaoViewRepository movimentacaoRepository;

    public List<ItemExtratoResponse> buscarMovimentacoesPorNumConta(String cpfConta){
        return movimentacaoRepository.findAllByCpfClienteOrigem(cpfConta);
    }
}
