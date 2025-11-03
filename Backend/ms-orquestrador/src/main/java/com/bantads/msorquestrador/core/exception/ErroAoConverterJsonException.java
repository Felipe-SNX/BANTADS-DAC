package com.bantads.msorquestrador.core.exception;

import lombok.Getter;

@Getter
public class ErroAoConverterJsonException extends RuntimeException {

    private String recurso;
    private String codigo;

    public ErroAoConverterJsonException(String message) {
        super(message);
    }

    public ErroAoConverterJsonException(String recurso, String codigo) {
        super(String.format("Erro em %s descrição: %s", recurso, codigo));
        this.recurso = recurso;
        this.codigo = codigo;
    }
}
