package com.bantads.msgerente.core.enums;

public enum ETopics {

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

