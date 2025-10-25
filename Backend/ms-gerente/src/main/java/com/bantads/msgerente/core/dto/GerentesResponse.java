package com.bantads.msgerente.core.dto;

import com.bantads.msgerente.core.enums.TipoGerente;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class GerentesResponse {

    private String cpf;
    private String nome;
    private String email;
    private TipoGerente tipo;
}
