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
public class ContaResponse {

    private Long idCliente;
    private String conta;
    private BigDecimal saldo;
    private BigDecimal limite;
    private Long idGerente;
    private LocalDateTime dataCriacao;
}
