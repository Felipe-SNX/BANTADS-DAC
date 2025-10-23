package com.bantads.msgerente.core.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class DadoGerenteInsercao {

    private String cpf;
    private String nome;
    private String email;
    private String tipo;
    private String senha;
}
