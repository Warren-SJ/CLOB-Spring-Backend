package com.warren.clob.config;

import org.springframework.amqp.core.*;
import org.springframework.amqp.support.converter.JacksonJsonMessageConverter;
import org.springframework.amqp.support.converter.MessageConverter;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class RabbitMQConfig {

    public static final String EXCHANGE_NAME = "clob.exchange";
    public static final String ORDER_QUEUE = "order.requests";
    public static final String TRADE_QUEUE = "trade.results";
    public static final String ORDER_ROUTING_KEY = "order.routing";
    public static final String TRADE_ROUTING_KEY = "trade.routing";

    @Bean
    public DirectExchange clobExchange() {
        return new DirectExchange(EXCHANGE_NAME);
    }

    @Bean
    public Queue orderRequestsQueue() {
        return QueueBuilder.durable(ORDER_QUEUE).quorum().build();
    }

    @Bean
    public Binding orderBinding(Queue orderRequestsQueue, DirectExchange clobExchange) {
        return BindingBuilder.bind(orderRequestsQueue).to(clobExchange).with(ORDER_ROUTING_KEY);
    }

    @Bean
    public Queue tradeResultsQueue() {
        return QueueBuilder.durable(TRADE_QUEUE).quorum().build();
    }

    @Bean
    public Binding tradeBinding(Queue tradeResultsQueue, DirectExchange clobExchange) {
        return BindingBuilder.bind(tradeResultsQueue).to(clobExchange).with(TRADE_ROUTING_KEY);
    }

    @Bean
    public MessageConverter jsonMessageConverter() {
        return new JacksonJsonMessageConverter();
    }
}