package com.bantads.msconta.event.dto;

import lombok.Data;

import java.math.BigDecimal;

@Data
public class DadosClienteConta {

    private String email;
    private String cliente;
    private String gerente;
    private BigDecimal salario;
}
