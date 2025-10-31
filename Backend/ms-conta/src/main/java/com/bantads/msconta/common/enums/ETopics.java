package com.bantads.msconta.common.enums;

public enum ETopics {

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

