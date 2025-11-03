package com.bantads.msauth.common.enums;

public enum ETopics {

    CMD_AUTH_CREATE("cmd.auth.create"),
    CMD_AUTH_COMPENSATE("cmd.auth.compensate"),

    EVT_AUTH_SUCCESS("evt.auth.success"),
    EVT_AUTH_FAIL("evt.auth.fail");

    private final String topic;

    ETopics(String topic) {
        this.topic = topic;
    }

    public String getTopic() {
        return this.topic;
    }
}
