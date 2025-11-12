package com.bantads.msconta.common.conta.exception;

import lombok.Getter;

@Getter
public class TransferenciaInvalidaException extends RuntimeException{

    private String recurso;
    private String codigo;

    public TransferenciaInvalidaException(String message) {
        super(message);
    }

    public TransferenciaInvalidaException(String recurso, String codigo) {
        super(String.format("O recurso %s com o identificador %s não foi encontrado.", recurso, codigo));
        this.recurso = recurso;
        this.codigo = codigo;
    }
}
