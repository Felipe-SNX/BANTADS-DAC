package com.bantads.msconta.core.service;

import com.bantads.msconta.core.dto.ItemExtratoResponse;
import com.bantads.msconta.core.model.Movimentacao;
import com.bantads.msconta.core.repository.ContaRepository;
import com.bantads.msconta.core.repository.MovimentacaoRepository;
import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.util.List;

@Slf4j
@Service
@AllArgsConstructor
public class MovimentacaoService {

    private final MovimentacaoRepository movimentacaoRepository;

    public void salvarMovimentacao(Movimentacao movimentacao){
        movimentacaoRepository.save(movimentacao);
    }

    public List<ItemExtratoResponse> buscarMovimentacoesPorNumConta(String cpfConta){
        return movimentacaoRepository.findAllByCpfClienteOrigem(cpfConta);
    }
}
