package com.bantads.msorquestrador.config.rabbitmq;

import org.springframework.amqp.core.Binding;
import org.springframework.amqp.core.BindingBuilder;
import org.springframework.amqp.core.Queue;
import org.springframework.amqp.core.TopicExchange;
import org.springframework.amqp.support.converter.Jackson2JsonMessageConverter;
import org.springframework.amqp.support.converter.MessageConverter;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class RabbitMQConfig {

    @Bean
    public TopicExchange topicExchange() {
        return new TopicExchange(RabbitMQConstantes.NOME_EXCHANGE);
    }

    @Bean
    public Queue orchestratorQueue() {
        return new Queue(RabbitMQConstantes.FILA_ORQUESTRADOR, true);
    }

    @Bean
    public Binding startSagaBinding(TopicExchange exchange, Queue orchestratorQueue) {
        return BindingBuilder
                .bind(orchestratorQueue)
                .to(exchange)
                .with(RabbitMQConstantes.ROUTING_KEY_START_SAGA);
    }

    @Bean
    public Binding replyEventsBinding(TopicExchange exchange, Queue orchestratorQueue) {
        return BindingBuilder
                .bind(orchestratorQueue)
                .to(exchange)
                .with(RabbitMQConstantes.ROUTING_KEY_EVENTOS);
    }

    @Bean
    public MessageConverter jsonMessageConverter() {
        return new Jackson2JsonMessageConverter();
    }
}