package com.bantads.mscliente.core.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;

@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class ClienteParaAprovarResponse {

    private String cpf;
    private String nome;
    private String email;
    private String endereco;
    private String cidade;
    private String estado;
}
