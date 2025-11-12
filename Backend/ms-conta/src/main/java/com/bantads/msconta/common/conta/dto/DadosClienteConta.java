package com.bantads.msconta.common.conta.dto;

import lombok.Data;

import java.math.BigDecimal;

@Data
public class DadosClienteConta {

    private String email;
    private String cliente;
    private String gerente;
    private BigDecimal salario;
}
