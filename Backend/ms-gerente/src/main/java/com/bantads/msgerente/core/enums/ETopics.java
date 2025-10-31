package com.bantads.msgerente.core.enums;

public enum ETopics {

    CMD_GERENTE_CREATE("cmd.gerente.create"),
    CMD_GERENTE_COMPENSATE("cmd.gerente.compensate"),

    EVT_GERENTE_SUCCESS("evt.gerente.success"),
    EVT_GERENTE_FAIL("evt.gerente.fail");

    private final String topic;

    ETopics(String topic) {
        this.topic = topic;
    }

    public String getTopic() {
        return this.topic;
    }
}

