package com.shortener.shortener.configuration;


import org.springframework.amqp.core.*;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class RabbitMqConfig {

    private static final boolean NON_DURABLE = false;

    public final static String TOPIC_QUEUE_1_NAME = "com.shortener.topic.queue1";
    public final static String TOPIC_QUEUE_2_NAME = "com.shortener.topic.queue2";
    public final static String TOPIC_EXCHANGE_NAME = "com.shortener.topic.exchange";
    public static final String BINDING_PATTERN_CREATE = "create";
    public static final String BINDING_PATTERN_DELETE = "delete";

    public final static String ROUTING_KEY_CREATE = "create";
    public final static String ROUTING_KEY_DELETE = "delete";

    @Bean
    public Declarables topicBindings() {
        Queue topicQueue1 = new Queue(TOPIC_QUEUE_1_NAME, NON_DURABLE);
        Queue topicQueue2 = new Queue(TOPIC_QUEUE_2_NAME, NON_DURABLE);

        TopicExchange topicExchange = new TopicExchange(TOPIC_EXCHANGE_NAME, NON_DURABLE, false);

        return new Declarables(topicQueue1, topicQueue2, topicExchange, BindingBuilder
                .bind(topicQueue1)
                .to(topicExchange)
                .with(BINDING_PATTERN_CREATE), BindingBuilder
                .bind(topicQueue2)
                .to(topicExchange)
                .with(BINDING_PATTERN_DELETE));
    }
}
