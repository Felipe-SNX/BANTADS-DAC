package com.bantads.msorquestrador.config.rabbitmq;

public class RabbitMQConstantes {

    private RabbitMQConstantes() {
    }

    public static final String NOME_EXCHANGE = "bantads.eventos";
    public static final String FILA_ORQUESTRADOR = "q_orquestrador_eventos";
    public static final String ROUTING_KEY_EVENTOS = "evt.#";  // A routing key "evt.#" captura todos os eventos de resposta
    public static final String ROUTING_KEY_START_SAGA = "*.saga.start";
}

