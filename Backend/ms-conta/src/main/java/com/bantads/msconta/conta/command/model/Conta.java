package com.bantads.msconta.conta.command.model;

import jakarta.persistence.*;
import lombok.*;

import java.math.BigDecimal;
import java.time.LocalDateTime;

import com.bantads.msconta.conta.exception.ValorInvalidoException;

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
    private String numConta;

    @Column(nullable = false, updatable = false)
    private LocalDateTime dataCriacao;

    @Column(nullable = false)
    private BigDecimal saldo;

    @Column(nullable = false)
    private BigDecimal limite;

    @Column(nullable = false)
    private String cpfCliente;

    private String cpfGerente;

    private boolean ativo = false;

    public Conta(String numConta, BigDecimal limite, String cpfCliente, String cpfGerente) {
        if (limite.compareTo(BigDecimal.ZERO) < 0) {
            throw new IllegalArgumentException("O limite não pode ser negativo.");
        }
        this.numConta = numConta;
        this.dataCriacao = LocalDateTime.now();
        this.saldo = BigDecimal.ZERO;
        this.limite = limite;
        this.cpfCliente = cpfCliente;
        this.cpfGerente = cpfGerente;
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
