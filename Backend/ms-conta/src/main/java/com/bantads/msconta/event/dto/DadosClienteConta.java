package com.bantads.msconta.event.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;

@Data
public class DadosClienteConta {

    private String cpfCliente;
    private String cpfGerente;
    private BigDecimal salario;
}
