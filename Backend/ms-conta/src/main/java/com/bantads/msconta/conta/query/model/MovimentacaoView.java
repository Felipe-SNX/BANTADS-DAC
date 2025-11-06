package com.bantads.msconta.conta.query.model;

import java.math.BigDecimal;
import java.time.LocalDateTime;

import com.bantads.msconta.conta.enums.TipoMovimentacao;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Entity
@Builder
@NoArgsConstructor
@AllArgsConstructor
@Table(name = "movimentacao_view", schema = "conta_r")
public class MovimentacaoView {

    @Id
    private Long id;

    @Column(nullable = false)
    private LocalDateTime data;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private TipoMovimentacao tipo;

    @Column(nullable = false)
    private String cpfClienteOrigem;

    @Column(nullable = true)
    private String cpfClienteDestino;

    @Column(nullable = false)
    private BigDecimal valor;

    private String numContaOrigem;
    private String destino;
}
