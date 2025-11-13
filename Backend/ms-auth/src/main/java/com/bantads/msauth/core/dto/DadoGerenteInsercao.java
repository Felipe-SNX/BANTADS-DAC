package com.bantads.msauth.core.dto;

import com.bantads.msauth.core.enums.TipoGerente;
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
    private String telefone;
    private TipoGerente tipo;
    private String senha;
}
