package com.bantads.mscliente.core.exception;

public class EnderecoNaoEncontradoException extends RuntimeException {

    private String recurso;
    private String codigo;

    public EnderecoNaoEncontradoException(String message) {
        super(message);
    }

    public EnderecoNaoEncontradoException(String recurso, String codigo) {
        super(String.format("O %s do cliente %s não foi encontrado.", recurso, codigo));
        this.recurso = recurso;
        this.codigo = codigo;
    }
}
