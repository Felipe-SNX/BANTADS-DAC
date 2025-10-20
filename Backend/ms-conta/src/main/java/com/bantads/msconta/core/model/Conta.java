package com.bantads.msconta.core.model;

import com.bantads.msconta.core.exception.ValorInvalidoException;
import jakarta.persistence.*;
import lombok.*;

import java.math.BigDecimal;
import java.time.LocalDateTime;

@Getter
@Entity
@NoArgsConstructor
@Table(name = "conta")
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

    @Column(nullable = false)
    private String cpfGerente;

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
