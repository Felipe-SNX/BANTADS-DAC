package com.bantads.msconta.common.conta.dto;

import java.math.BigDecimal;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class PerfilInfo {

    private String nome;
    private String email;
    private BigDecimal salario;
    private String endereco;
    private String telefone;
    private String cep;
    private String cidade;
    private String estado;
}
