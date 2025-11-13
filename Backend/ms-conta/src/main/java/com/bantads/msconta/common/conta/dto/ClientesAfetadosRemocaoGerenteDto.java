package com.bantads.msconta.common.conta.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class ClientesAfetadosRemocaoGerenteDto {

    private String cliente;
    private String gerenteAntigo;
    private String gerenteNovo;
}
