package com.bantads.msconta.common.conta.dto;

import java.math.BigDecimal;
import java.time.LocalDateTime;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class ContaSyncEvento {
    
    private long id; 
    private String conta;
    private String cliente;
    private String gerente;
    private BigDecimal saldo;
    private BigDecimal limite;
    private LocalDateTime dataCriacao;
}
