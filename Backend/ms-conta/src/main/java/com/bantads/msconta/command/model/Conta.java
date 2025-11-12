package com.bantads.msconta.command.model;

import jakarta.persistence.*;
import lombok.*;

import java.math.BigDecimal;
import java.time.LocalDateTime;

import com.bantads.msconta.common.conta.exception.ValorInvalidoException;

@Data
@Builder
@Entity
@AllArgsConstructor
@NoArgsConstructor
@Table(name = "conta", schema = "conta_cud")
public class Conta {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false)
    private String conta;

    @Column(nullable = false, updatable = false)
    private LocalDateTime dataCriacao;

    @Column(nullable = false)
    private BigDecimal saldo;

    @Column(nullable = false)
    private BigDecimal limite;

    @Column(nullable = false)
    private String cliente;

    private String gerente;

    public Conta(String conta, BigDecimal limite, String cliente, String gerente, LocalDateTime dataCriacao) {
        if (limite.compareTo(BigDecimal.ZERO) < 0) {
            throw new IllegalArgumentException("O limite não pode ser negativo.");
        }
        this.conta = conta;
        this.dataCriacao = dataCriacao;
        this.saldo = BigDecimal.ZERO;
        this.limite = limite;
        this.cliente = cliente;
        this.gerente = gerente;
    }

    public void depositar(BigDecimal valor) {
        if (valor == null || valor.compareTo(BigDecimal.ZERO) <= 0) {
            throw new ValorInvalidoException("O valor de depósito/transferência não pode ser nulo e deve ser positivo.");
        }
        this.saldo = this.saldo.add(valor);
    }

    public void sacar(BigDecimal valor) {
        if (valor == null || valor.compareTo(BigDecimal.ZERO) <= 0) {
            throw new ValorInvalidoException("O valor de saque não pode ser nulo e deve ser positivo.");
        }
        if (this.saldo.add(this.limite).compareTo(valor) < 0) {
            throw new ValorInvalidoException("Valor insuficiente para sacar/transferir");
        }
        this.saldo = this.saldo.subtract(valor);
    }
}
