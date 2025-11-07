package com.bantads.msgerente.core.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class GerenteNumeroContasDto {

    private String cpfGerente;
    private Long quantidade;
}
