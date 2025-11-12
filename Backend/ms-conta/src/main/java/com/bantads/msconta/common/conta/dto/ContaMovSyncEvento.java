package com.bantads.msconta.common.conta.dto;

import java.io.Serializable;
import java.math.BigDecimal;
import java.time.LocalDateTime;

import com.bantads.msconta.command.model.Movimentacao;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;

@Data
@Builder
@AllArgsConstructor
public class ContaMovSyncEvento implements Serializable{

    private Long id;
    private String conta;
    private LocalDateTime dataCriacao;
    private BigDecimal saldo;
    private BigDecimal limite;
    private String cliente;
    private String gerente;
    private boolean ativo;
    private Long contaIdOrigem;
    private BigDecimal novoSaldoOrigem;
    private Long contaIdDestino;
    private BigDecimal novoSaldoDestino;
    private Movimentacao movimentacao;
}
