package com.bantads.msconta.core.dto;

import java.io.Serializable;
import java.math.BigDecimal;

import com.bantads.msconta.core.model.Movimentacao;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;

@Data
@Builder
@AllArgsConstructor
public class MovimentacaoRealizadaEvent implements Serializable{

    private Long id;
    private BigDecimal novoSaldo;
    private Movimentacao movimentacao;
}
