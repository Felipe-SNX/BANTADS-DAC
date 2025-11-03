package com.bantads.mscliente.core.enums;

public enum ETopics {

    CMD_CLIENTE_CREATE("cmd.cliente.create"),
    CMD_CLIENTE_COMPENSATE("cmd.cliente.compensate"),

    EVT_CLIENTE_SUCCESS("evt.cliente.success"),
    EVT_CLIENTE_FAIL("evt.cliente.fail");

    private final String topic;

    ETopics(String topic) {
        this.topic = topic;
    }

    public String getTopic() {
        return this.topic;
    }
}

