package com.bantads.msconta.core.service;

import java.util.ArrayList;
import java.util.List;

import com.bantads.msconta.core.model.MovimentacaoView;
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

    public List<ItemExtratoResponse> buscarMovimentacoesPorCpf(String cpfConta){
        List<MovimentacaoView> movimentacoes = movimentacaoRepository.findAllByCpfClienteOrigem(cpfConta);

        List<ItemExtratoResponse> itemExtratoResponses = new ArrayList<>();

        for(MovimentacaoView movimentacaoView : movimentacoes){
            var itemExtratoResponse = ItemExtratoResponse
                    .builder()
                    .data(movimentacaoView.getData())
                    .tipo(movimentacaoView.getTipo())
                    .numContaOrigem(movimentacaoView.getNumContaOrigem())
                    .numContaDestino(movimentacaoView.getNumContaDestino())
                    .valor(movimentacaoView.getValor())
                    .build();

            itemExtratoResponses.add(itemExtratoResponse);
        }

        return itemExtratoResponses;
    }
}
