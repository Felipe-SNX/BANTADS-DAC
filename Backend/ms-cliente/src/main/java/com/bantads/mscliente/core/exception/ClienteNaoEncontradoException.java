package com.bantads.mscliente.core.exception;

import lombok.Getter;

@Getter
public class ClienteNaoEncontradoException extends RuntimeException {

    private String recurso;
    private String codigo;

    public ClienteNaoEncontradoException(String message) {
        super(message);
    }

    public ClienteNaoEncontradoException(String recurso, String codigo) {
        super(String.format("O %s de cpf %s não foi encontrado.", recurso, codigo));
        this.recurso = recurso;
        this.codigo = codigo;
    }
}
