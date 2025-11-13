package com.bantads.msconta.common.conta.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class ContaEscolhidaDto {
    private String gerente;
    private String cliente;
    private String conta;
}
