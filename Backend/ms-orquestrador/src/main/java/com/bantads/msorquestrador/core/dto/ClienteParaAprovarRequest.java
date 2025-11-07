package com.bantads.msorquestrador.core.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class ClienteParaAprovarRequest {

    private String cpf;
    private String nome;
    private String email;
}
