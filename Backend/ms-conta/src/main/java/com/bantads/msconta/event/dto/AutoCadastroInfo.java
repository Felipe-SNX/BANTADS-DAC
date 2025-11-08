package com.bantads.msconta.event.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;

@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class AutoCadastroInfo {

    private String cpf;
    private String email;
    private String nome;
    private BigDecimal salario;
    private String telefone;
    private String endereco;
    private String cep;
    private String cidade;
    private String estado;
}
