package com.bantads.msconta.config.rabbitmq.connection;

import org.springframework.amqp.core.AmqpAdmin;
import org.springframework.amqp.core.Binding;
import org.springframework.amqp.core.DirectExchange;
import org.springframework.amqp.core.Queue;
import org.springframework.stereotype.Component;

import com.bantads.msconta.config.rabbitmq.constantes.RabbitMQConstantes;

import jakarta.annotation.PostConstruct;

@Component
public class RabbitMQConnection {

    private static final String NOME_EXCHANGE = "bantads.eventos";
    private AmqpAdmin amqpAdmin;

    public RabbitMQConnection(AmqpAdmin amqpAdmin){
        this.amqpAdmin = amqpAdmin;
    }

    private Queue fila(String nomeFila){
        return new Queue(nomeFila, true, false, false);
    }

    private DirectExchange trocaDireta(){
        return new DirectExchange(NOME_EXCHANGE);
    }

    private Binding relacionamento(Queue fila, DirectExchange troca){
        return new Binding(fila.getName(), Binding.DestinationType.QUEUE, troca.getName(), fila.getName(), null);
    }

    @PostConstruct
    private void adiciona(){
        Queue filaContaSync = this.fila(RabbitMQConstantes.FILA_CONTA_SYNC);
        Queue filaMovSync = this.fila(RabbitMQConstantes.FILA_MOV_SYNC);

        DirectExchange troca = this.trocaDireta();

        Binding bindingContaSync = this.relacionamento(filaContaSync, troca);
        Binding bindingMovSync = this.relacionamento(filaMovSync, troca);

        //Criação de filas
        this.amqpAdmin.declareQueue(filaContaSync);
        this.amqpAdmin.declareQueue(filaMovSync);

        //Criação exchange
        this.amqpAdmin.declareExchange(troca);
        
        //Criação Bindings
        this.amqpAdmin.declareBinding(bindingContaSync);
        this.amqpAdmin.declareBinding(bindingMovSync);
    }
}
