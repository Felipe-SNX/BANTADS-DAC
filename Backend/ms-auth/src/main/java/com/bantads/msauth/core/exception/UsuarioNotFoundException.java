package com.bantads.msauth.core.exception;

import lombok.Getter;

@Getter
public class UsuarioNotFoundException extends RuntimeException{

    private String recurso;
    private String codigo;

    public UsuarioNotFoundException(String message) {
        super(message);
    }

    public UsuarioNotFoundException(String recurso, String codigo) {
        super(String.format("O %s com o login %s não foi encontrado.", recurso, codigo));
        this.recurso = recurso;
        this.codigo = codigo;
    }
}
