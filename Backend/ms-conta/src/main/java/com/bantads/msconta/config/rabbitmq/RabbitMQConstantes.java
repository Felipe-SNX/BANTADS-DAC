package com.bantads.msconta.config.rabbitmq;

public class RabbitMQConstantes {

    private RabbitMQConstantes() {
    }

    public static final String NOME_EXCHANGE = "bantads.eventos";
    public static final String FILA_CONTA_SYNC = "conta.sync";
    public static final String ROUTING_KEY = "conta.movimentacao.realizada";
}
