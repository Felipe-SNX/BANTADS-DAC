package com.bantads.mscliente.core.dto;

import lombok.Data;
import java.math.BigDecimal;

@Data
public class ClienteDto {

    private Long id;
    private String nome;
    private String email;
    private String cpf;
    private BigDecimal salario;
}
