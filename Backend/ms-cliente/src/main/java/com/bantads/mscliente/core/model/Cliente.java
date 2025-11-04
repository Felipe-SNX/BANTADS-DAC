package com.bantads.mscliente.core.model;

import jakarta.persistence.*;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;

@Data
@Entity
@NoArgsConstructor
@Table(name = "cliente", schema = "ms_cliente")
public class Cliente {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false)
    private String nome;

    @Column(nullable = false)
    private String email;

    @Column(nullable = false)
    private String cpf;

    @Column(nullable = false)
    private long idEndereco;

    private String telefone;

    @Column(nullable = false)
    private BigDecimal salario;

    private String cpfGerente;

    @Column(nullable = false)
    private boolean aprovado = false;

    private String motivoRejeição = null;
}
