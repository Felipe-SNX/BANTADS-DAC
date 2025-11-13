package com.bantads.msconta.common.saga.enums;

public enum ETopics {

    CMD_CONTA_CREATE("cmd.conta.create"),
    CMD_CONTA_COMPENSATE("cmd.conta.compensate"),

    EVT_CONTA_SUCCESS("evt.conta.success"),
    EVT_CONTA_FAIL("evt.conta.fail");

    private final String topic;

    ETopics(String topic) {
        this.topic = topic;
    }

    public String getTopic() {
        return this.topic;
    }
}

