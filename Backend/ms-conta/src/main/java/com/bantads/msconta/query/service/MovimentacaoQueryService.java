package com.bantads.msconta.query.service;

import java.util.ArrayList;
import java.util.List;

import org.springframework.stereotype.Service;

import com.bantads.msconta.common.conta.dto.ItemExtratoResponse;
import com.bantads.msconta.query.model.MovimentacaoView;
import com.bantads.msconta.query.repository.MovimentacaoViewRepository;

import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@Slf4j
@Service
@AllArgsConstructor
public class MovimentacaoQueryService {

    private final MovimentacaoViewRepository movimentacaoRepository;

    public List<ItemExtratoResponse> buscarMovimentacoesPorCpf(String cpfConta){
        List<MovimentacaoView> movimentacoes = movimentacaoRepository.findAllByCpfCliente(cpfConta);

        List<ItemExtratoResponse> itemExtratoResponses = new ArrayList<>();

        for(MovimentacaoView movimentacaoView : movimentacoes){
            var itemExtratoResponse = ItemExtratoResponse
                    .builder()
                    .data(movimentacaoView.getData())
                    .tipo(movimentacaoView.getTipo())
                    .origem(movimentacaoView.getOrigem())
                    .destino(movimentacaoView.getDestino())
                    .valor(movimentacaoView.getValor())
                    .build();

            itemExtratoResponses.add(itemExtratoResponse);
        }

        return itemExtratoResponses;
    }
}
