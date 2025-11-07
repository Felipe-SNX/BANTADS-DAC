package com.bantads.msauth.core.dto;

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
