package com.bantads.msconta.event.dto;

import java.io.Serializable;
import java.math.BigDecimal;
import java.time.LocalDateTime;

import com.bantads.msconta.conta.command.model.Movimentacao;

import jakarta.persistence.Column;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;

@Data
@Builder
@AllArgsConstructor
public class ContaSyncEvento implements Serializable{

    private Long id;
    private String numConta;
    private LocalDateTime dataCriacao;
    private BigDecimal saldo;
    private BigDecimal limite;
    private String cpfCliente;
    private String cpfGerente;
    private boolean ativo;
    private Long contaIdOrigem;
    private BigDecimal novoSaldoOrigem;
    private Long contaIdDestino;
    private BigDecimal novoSaldoDestino;
    private Movimentacao movimentacao;
}
