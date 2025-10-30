package com.bantads.msorquestrador.core.dto;

import java.math.BigDecimal;

public record PerfilInfo(
    String nome,
    String email,
    BigDecimal salario,
    String endereco,
    String cep,
    String cidade,
    String estado
) {}
