package com.bantads.msconta.conta.exception;

import lombok.Getter;

@Getter
public class ValorInvalidoException extends RuntimeException{

    private String recurso;
    private String codigo;

    public ValorInvalidoException(String message) {
        super(message);
    }

    public ValorInvalidoException(String recurso, String codigo) {
        this.recurso = recurso;
        this.codigo = codigo;
    }
}
