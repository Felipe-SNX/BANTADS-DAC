package com.bantads.msconta.conta.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;
import java.time.LocalDateTime;

import com.bantads.msconta.conta.enums.TipoMovimentacao;

@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class ItemExtratoResponse {

    private LocalDateTime data;
    private TipoMovimentacao tipo;
    private String origem;
    private String destino;
    private BigDecimal valor;
}
