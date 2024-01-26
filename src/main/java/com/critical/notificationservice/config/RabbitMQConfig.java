package com.critical.notificationservice.config;

import lombok.extern.slf4j.Slf4j;
import org.springframework.amqp.core.Queue;
import org.springframework.amqp.core.TopicExchange;
import org.springframework.amqp.rabbit.connection.CachingConnectionFactory;
import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.amqp.support.converter.Jackson2JsonMessageConverter;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
@Slf4j
public class RabbitMQConfig {

    private final CachingConnectionFactory cachingConnectionFactory;

    @Value("${catalog.rabbitmq.queue-book-stock-request}")
    String queueBookStockRequestName;

    @Value("${catalog.rabbitmq.queue.exchange}")
    private String exchange;

    @Value("${catalog.rabbitmq.queue.routing.key}")
    private String routingKey;

    public RabbitMQConfig(CachingConnectionFactory cachingConnectionFactory) {

        this.cachingConnectionFactory = cachingConnectionFactory;
    }

    @Bean
    public Queue CreateBookStockRequestQueue() {

        return new Queue(queueBookStockRequestName);
    }

    @Bean
    public TopicExchange exchange() {

        return new TopicExchange(exchange);
    }

    @Bean
    public Jackson2JsonMessageConverter jsonMessageConverter() {

        return new Jackson2JsonMessageConverter();
    }

    @Bean
    public RabbitTemplate rabbitTemplate(Jackson2JsonMessageConverter converter) {

        RabbitTemplate template = new RabbitTemplate(cachingConnectionFactory);
        template.setMessageConverter(converter);
        return template;
    }
}