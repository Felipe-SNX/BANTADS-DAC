package com.bantads.msgerente.config.rabbitmq;

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
    public Queue gerentesQueue() {
        return new Queue(RabbitMQConstantes.FILA_GERENTES, true);
    }

    @Bean
    public Binding gerentesBinding(TopicExchange exchange, Queue gerentesQueue) {
        return BindingBuilder
                .bind(gerentesQueue)
                .to(exchange)
                .with(RabbitMQConstantes.ROUTING_KEY);
    }

    @Bean
    public MessageConverter jsonMessageConverter() {
        return new Jackson2JsonMessageConverter();
    }
}

