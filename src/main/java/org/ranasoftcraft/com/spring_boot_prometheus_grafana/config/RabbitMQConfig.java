package org.ranasoftcraft.com.spring_boot_prometheus_grafana.config;

import org.springframework.amqp.core.Binding;
import org.springframework.amqp.core.BindingBuilder;
import org.springframework.amqp.core.Queue;
import org.springframework.amqp.core.TopicExchange;
import org.springframework.amqp.rabbit.connection.ConnectionFactory;
import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.amqp.support.converter.Jackson2JsonMessageConverter;
import org.springframework.amqp.support.converter.MessageConverter;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class RabbitMQConfig {

    @Value("${rabbitmq.queue.first}")
    private String firstQueueName;

    @Value("${rabbitmq.queue.second}")
    private String secondQueueName;

    @Value("${rabbitmq.exchange}")
    private String exchangeName;

    @Value("${rabbitmq.routing-key.first}")
    private String firstRoutingKey;

    @Value("${rabbitmq.routing-key.second}")
    private String secondRoutingKey;

    @Bean
    public Queue firstQueue() {
        return new Queue(firstQueueName, true); // durable queue
    }

    @Bean
    public Queue secondQueue() {
        return new Queue(secondQueueName, true); // durable queue
    }

    @Bean
    public TopicExchange exchange() {
        return new TopicExchange(exchangeName);
    }

    @Bean
    public Binding firstQueueBinding(Queue firstQueue, TopicExchange exchange) {
        return BindingBuilder
                .bind(firstQueue)
                .to(exchange)
                .with(firstRoutingKey);
    }

    @Bean
    public Binding secondQueueBinding(Queue secondQueue, TopicExchange exchange) {
        return BindingBuilder
                .bind(secondQueue)
                .to(exchange)
                .with(secondRoutingKey);
    }

    @Bean
    public MessageConverter jsonMessageConverter() {
        return new Jackson2JsonMessageConverter();
    }

    @Bean
    public RabbitTemplate rabbitTemplate(ConnectionFactory connectionFactory) {
        RabbitTemplate rabbitTemplate = new RabbitTemplate(connectionFactory);
//        rabbitTemplate.setMessageConverter(jsonMessageConverter());
        return rabbitTemplate;
    }
}
