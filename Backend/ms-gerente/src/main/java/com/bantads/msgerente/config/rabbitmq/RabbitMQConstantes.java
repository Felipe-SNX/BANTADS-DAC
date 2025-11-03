package com.bantads.msgerente.config.rabbitmq;

public class RabbitMQConstantes {

    private RabbitMQConstantes() {
    }

    public static final String NOME_EXCHANGE = "bantads.eventos";
    public static final String FILA_GERENTES = "q_gerentes_eventos";
    public static final String ROUTING_KEY = "cmd.gerente.#";
}
