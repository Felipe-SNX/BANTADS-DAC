package com.bantads.msconta.common.saga.dto;

import com.bantads.msconta.common.saga.enums.TipoGerente;

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
    private TipoGerente tipo;
    private String senha;
}
