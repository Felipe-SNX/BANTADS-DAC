package com.bantads.msorquestrador.core.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;

@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class PerfilInfoResponse{

    private String cpf;
    private String nome;
    private String email;
    private BigDecimal salario;
    private String endereco;
    private String telefone;
    private String cep;
    private String cidade;
    private String estado;
}
