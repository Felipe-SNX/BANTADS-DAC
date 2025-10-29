package com.bantads.msorquestrador.config.rabbitmq;

public class RabbitMQConstantes {

    private RabbitMQConstantes() {
    }

    public static final String NOME_EXCHANGE = "bantads.eventos";
    public static final String FILA = "q_orquestrador_eventos";
    public static final String ROUTING_KEY = "#";
}

