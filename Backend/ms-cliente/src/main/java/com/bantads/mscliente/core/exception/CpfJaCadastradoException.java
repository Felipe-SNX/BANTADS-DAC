package com.bantads.mscliente.core.exception;

import lombok.Getter;

@Getter
public class CpfJaCadastradoException extends RuntimeException{

    private String recurso;
    private String codigo;

    public CpfJaCadastradoException(String message) {
            super(message);
    }

    public CpfJaCadastradoException(String recurso, String codigo) {
        super(String.format("O %s com o cpf %s já está cadastrado no sistema.", recurso, codigo));
        this.recurso = recurso;
        this.codigo = codigo;
    }
}

