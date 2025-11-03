package com.bantads.msorquestrador.core.dto;

import java.math.BigDecimal;

public record AutoCadastroInfo(
    String cpf,
    String email,
    String nome,
    BigDecimal salario,
    String endereco,
    String cep,
    String cidade,
    String estado
) {}
