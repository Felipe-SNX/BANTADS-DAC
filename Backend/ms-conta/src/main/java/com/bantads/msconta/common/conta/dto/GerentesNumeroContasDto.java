package com.bantads.msconta.common.conta.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class GerentesNumeroContasDto {

    private String cpfGerente;
    private Long quantidade;
}
