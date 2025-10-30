package com.bantads.mscliente.config.rabbitmq;

public class RabbitMQConstantes {

    private RabbitMQConstantes() {
    }

    public static final String NOME_EXCHANGE = "bantads.eventos";
    public static final String FILA_CLIENTES = "q_clientes_eventos";
    public static final String ROUTING_KEY_AUTOCADASTRO_START_SAGA = "autocadastro.saga.start";
    public static final String ROUTING_KEY_ALTERACAO_PERFIL_START_SAGA = "alteracaoperfil.saga.start";
    public static final String ROUTING_KEY = "#";
}
