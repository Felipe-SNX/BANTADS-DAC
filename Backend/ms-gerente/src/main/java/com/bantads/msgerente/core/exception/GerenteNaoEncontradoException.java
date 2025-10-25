package com.bantads.msgerente.core.exception;

import lombok.Getter;

@Getter
public class GerenteNaoEncontradoException extends RuntimeException{

    private String recurso;
    private String codigo;

    public GerenteNaoEncontradoException(String message) {
        super(message);
    }

    public GerenteNaoEncontradoException(String recurso, String codigo) {
        super(String.format("O %s com o cpf %s não foi encontrado.", recurso, codigo));
        this.recurso = recurso;
        this.codigo = codigo;
    }
}
