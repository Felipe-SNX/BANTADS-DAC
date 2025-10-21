package com.bantads.msconta.config.rabbitmq;

import org.springframework.amqp.core.Binding;
import org.springframework.amqp.core.BindingBuilder;
import org.springframework.amqp.core.Queue;
import org.springframework.amqp.core.TopicExchange;
import org.springframework.amqp.support.converter.Jackson2JsonMessageConverter;
import org.springframework.amqp.support.converter.MessageConverter;
import org.springframework.context.annotation.Bean;
import org.springframework.stereotype.Component;

@Component
public class RabbitMQConfig {

    @Bean
    public TopicExchange bantadsExchange() {
        return new TopicExchange(RabbitMQConstantes.NOME_EXCHANGE);
    }

    @Bean
    public Queue fila(){
        return new Queue(RabbitMQConstantes.FILA_CONTA_SYNC, true, false, false);
    }

    @Bean
    public Binding contaSyncBinding(Queue contaSyncQueue, TopicExchange bantadsExchange) {

        return BindingBuilder.bind(contaSyncQueue)
                .to(bantadsExchange)
                .with(RabbitMQConstantes.ROUTING_KEY);
    }

    @Bean
    public MessageConverter jsonMessageConverter() {
        return new Jackson2JsonMessageConverter();
    }
}
