package com.bantads.mscliente.core.dto;

import com.bantads.mscliente.core.model.Cliente;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class DadosClienteConta {

    private String cpfCliente;
    private String cpfGerente;
    private BigDecimal salario;
}
