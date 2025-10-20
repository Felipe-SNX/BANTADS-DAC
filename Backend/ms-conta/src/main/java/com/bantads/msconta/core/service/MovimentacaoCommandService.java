package com.bantads.msconta.core.service;

import com.bantads.msconta.core.model.Movimentacao;
import com.bantads.msconta.core.repository.MovimentacaoWriteRepository;
import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

@Slf4j
@Service
@AllArgsConstructor
public class MovimentacaoCommandService {

    private final MovimentacaoWriteRepository movimentacaoRepository;

    public void salvarMovimentacao(Movimentacao movimentacao){
        movimentacaoRepository.save(movimentacao);
    }

}
