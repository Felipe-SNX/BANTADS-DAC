package com.bantads.msorquestrador.core.enums;

public enum ETopics {

    CMD_CLIENTE_CREATE("cmd.cliente.create"),
    CMD_CLIENTE_COMPENSATE("cmd.cliente.compensate"),

    CMD_AUTENTICACAO_CREATE("cmd.autenticacao.create"),
    CMD_AUTENTICACAO_COMPENSATE("cmd.autenticacao.compensate"),

    CMD_GERENTE_CREATE("cmd.gerente.create"),
    CMD_GERENTE_COMPENSATE("cmd.gerente.compensate"),

    CMD_CONTA_CREATE("cmd.conta.create"),
    CMD_CONTA_COMPENSATE("cmd.conta.compensate"),
    
    FINISH_SUCCESS("finish.success"),
    FINISH_FAIL("finish.fail"),
    NOTIFY_ENDING("notify.ending");

    private final String topic;

    ETopics(String topic) {
        this.topic = topic;
    } 

    public String getTopic() {
        return this.topic;
    }
}

