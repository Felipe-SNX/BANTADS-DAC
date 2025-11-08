package com.bantads.msauth.core.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class DadosClienteConta {

    private String email;
    private String cliente;
    private String gerente;
    private BigDecimal salario;
}
