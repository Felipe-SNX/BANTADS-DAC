package com.bantads.msorquestrador.core.enums;

public enum ETopics {

    FINISH_SUCCESS("finish-success"),
    FINISH_FAIL("finish-fail"),
    CLIENTE_SUCCESS("cliente-success"),
    CLIENTE_FAIL("cliente-fail"),
    AUTENTICACAO_SUCCESS("autenticacao-success"),
    AUTENTICACAO_FAIL("autenticacao-fail"),
    CONTA_SUCCESS("conta-success"),
    CONTA_FAIL("conta-fail"),
    GERENTE_SUCCESS("gerente-success"),
    GERENTE_FAIL("gerente-fail"),
    NOTIFY_ENDING("notify-ending");

    private final String topic;

    ETopics(String topic) {
        this.topic = topic;
    } 

    public String getTopic() {
        return this.topic;
    }
}

