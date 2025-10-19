package com.bantads.msconta.core.dto;

import com.bantads.msconta.core.enums.TipoMovimentacao;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;
import java.time.LocalDateTime;

@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class ItemExtratoResponse {

    private LocalDateTime data;
    private TipoMovimentacao tipo;
    private String numContaOrigem;
    private String numContaDestino;
    private BigDecimal valor;
}
