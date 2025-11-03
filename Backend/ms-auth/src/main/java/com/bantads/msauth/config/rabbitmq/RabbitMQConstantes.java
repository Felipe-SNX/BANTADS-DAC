package com.bantads.msauth.config.rabbitmq;

public class RabbitMQConstantes {

    private RabbitMQConstantes() {
    }

    public static final String NOME_EXCHANGE = "bantads.eventos";
    public static final String FILA_AUTH = "q_auth_eventos";
    public static final String ROUTING_KEY = "cmd.auth.#";
}
