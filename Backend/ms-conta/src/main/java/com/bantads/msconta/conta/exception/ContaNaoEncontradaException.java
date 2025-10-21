package com.bantads.msconta.conta.exception;

import lombok.Getter;

@Getter
public class ContaNaoEncontradaException extends RuntimeException{

    private String recurso;
    private String codigo;

    public ContaNaoEncontradaException(String message) {
        super(message);
    }

    public ContaNaoEncontradaException(String recurso, String codigo) {
        super(String.format("O recurso %s com o identificador %s não foi encontrado.", recurso, codigo));
        this.recurso = recurso;
        this.codigo = codigo;
    }
}
