package com.bantads.msconta.common.conta.exception;

public class ErroExecucaoSaga extends RuntimeException{

    private String recurso;
    private String codigo;

    public ErroExecucaoSaga(String message) {
        super(message);
    }

    public ErroExecucaoSaga(String recurso, String codigo) {
        super(String.format("Erro ao executar a saga %s do tipo %s", recurso, codigo));
        this.recurso = recurso;
        this.codigo = codigo;
    }
}
