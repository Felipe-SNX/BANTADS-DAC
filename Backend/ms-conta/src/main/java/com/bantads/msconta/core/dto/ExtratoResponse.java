package com.bantads.msconta.core.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;
import java.util.List;

@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class ExtratoResponse {

    private String numConta;
    private BigDecimal saldo;
    private List<ItemExtratoResponse> movimentacoes;
}
