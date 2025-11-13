package com.bantads.msconta.common.conta.dto;

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
public class DadoConta {

    private String cliente;
    private String conta;
    private BigDecimal saldo;
    private BigDecimal limite;
    private String gerente;
    private LocalDateTime dataCriacao;
}
