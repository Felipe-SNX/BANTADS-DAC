package com.bantads.msconta.command.service;

import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import com.bantads.msconta.command.model.Movimentacao;
import com.bantads.msconta.command.repository.MovimentacaoWriteRepository;

@Slf4j
@Service
@AllArgsConstructor
public class MovimentacaoCommandService {

    private final MovimentacaoWriteRepository movimentacaoRepository;

    public Movimentacao salvarMovimentacao(Movimentacao movimentacao){
        return movimentacaoRepository.save(movimentacao);
    }

}
