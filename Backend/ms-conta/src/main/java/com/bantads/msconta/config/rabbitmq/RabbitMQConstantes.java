package com.bantads.msconta.config.rabbitmq;

public class RabbitMQConstantes {

    private RabbitMQConstantes() {
    }

    public static final String NOME_EXCHANGE = "bantads.eventos";
    public static final String FILA_CONTA_CMD = "q_conta_eventos";
    public static final String FILA_CONTA_SYNC = "q_conta.sync";
    public static final String ROUTING_KEY_CMD = "cmd.conta.#";
    public static final String ROUTING_KEY_SYNC = "conta.movimentacao.realizada";

}
