package com.bantads.msconta.conta.dto;

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
public class TransferenciaResponse {

    private String conta;
    private LocalDateTime data;
    private String numContaDestino;
    private BigDecimal saldo;
    private BigDecimal valor;
}
