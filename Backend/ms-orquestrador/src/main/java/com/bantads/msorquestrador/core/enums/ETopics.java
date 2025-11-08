package com.bantads.msorquestrador.core.enums;

public enum ETopics {

    CMD_CLIENTE_CREATE("cmd.cliente.create"),
    CMD_CLIENTE_SET_GERENTE("cmd.cliente.set.gerente"),
    CMD_CLIENTE_COMPENSATE("cmd.cliente.compensate"),

    CMD_AUTH_CREATE("cmd.auth.create"),
    CMD_AUTH_COMPENSATE("cmd.auth.compensate"),

    CMD_GERENTE_CREATE("cmd.gerente.create"),
    CMD_GERENTE_COMPENSATE("cmd.gerente.compensate"),

    CMD_CONTA_CREATE("cmd.conta.create"),
    CMD_CONTA_COMPENSATE("cmd.conta.compensate"),

    EVT_CLIENTE_SUCCESS("evt.cliente.success"),
    EVT_CLIENTE_FAIL("evt.cliente.fail"),

    EVT_AUTH_SUCCESS("evt.auth.success"),
    EVT_AUTH_FAIL("evt.auth.fail"),

    EVT_GERENTE_SUCCESS("evt.gerente.success"),
    EVT_GERENTE_FAIL("evt.gerente.fail"),

    EVT_CONTA_SUCCESS("evt.conta.success"),
    EVT_CONTA_FAIL("evt.conta.fail"),
    
    FINISH_SUCCESS("finish.success"),
    FINISH_FAIL("finish.fail");

    private final String topic;

    ETopics(String topic) {
        this.topic = topic;
    } 

    public String getTopic() {
        return this.topic;
    }
}

