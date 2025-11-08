package com.bantads.msconta.config.rabbitmq;

import org.springframework.amqp.core.Binding;
import org.springframework.amqp.core.BindingBuilder;
import org.springframework.amqp.core.Queue;
import org.springframework.amqp.core.TopicExchange;
import org.springframework.amqp.support.converter.Jackson2JsonMessageConverter;
import org.springframework.amqp.support.converter.MessageConverter;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.context.annotation.Bean;
import org.springframework.stereotype.Component;

@Component
public class RabbitMQConfig {

    @Bean
    public TopicExchange bantadsExchange() {
        return new TopicExchange(RabbitMQConstantes.NOME_EXCHANGE);
    }

    @Bean
    @Qualifier("filaCqrs")
    public Queue filaCqrs(){
        return new Queue(RabbitMQConstantes.FILA_CONTA_SYNC, true, false, false);
    }

    @Bean
    @Qualifier("filaSaga")
    public Queue filaSaga(){
        return new Queue(RabbitMQConstantes.FILA_CONTA_CMD, true, false, false);
    }

    @Bean
    public Binding contaSagaBinding(TopicExchange exchange, @Qualifier("filaSaga") Queue contaSagaQueue) {
        return BindingBuilder
                .bind(contaSagaQueue)
                .to(exchange)
                .with(RabbitMQConstantes.ROUTING_KEY_CMD); 
    }

    @Bean
    public Binding contaCqrsBinding(@Qualifier("filaCqrs") Queue contaSyncQueue, TopicExchange bantadsExchange) {

        return BindingBuilder.bind(contaSyncQueue)
                .to(bantadsExchange)
                .with(RabbitMQConstantes.ROUTING_KEY_MOV);
    }

    @Bean
    public MessageConverter jsonMessageConverter() {
        return new Jackson2JsonMessageConverter();
    }
}
