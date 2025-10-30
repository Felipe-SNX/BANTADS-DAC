package com.bantads.msorquestrador.core.dto;

import com.bantads.msorquestrador.core.enums.TipoGerente;

public record DadoGerenteInsercao(
    
    String cpf,
    String nome,
    String email,
    TipoGerente tipo,
    String senha
) {}
