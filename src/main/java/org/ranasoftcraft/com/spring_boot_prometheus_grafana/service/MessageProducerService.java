package org.ranasoftcraft.com.spring_boot_prometheus_grafana.service;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

@Slf4j
@Service
@RequiredArgsConstructor
public class MessageProducerService {

    private final RabbitTemplate rabbitTemplate;

    @Value("${rabbitmq.exchange}")
    private String exchange;

    @Value("${rabbitmq.routing-key.first}")
    private String firstRoutingKey;

    @Value("${rabbitmq.routing-key.second}")
    private String secondRoutingKey;

    /**
     * Send message to the first queue
     */
    public void sendToFirstQueue(String message) {
        log.info("📤 Sending message to first queue: {}", message);
        rabbitTemplate.convertAndSend(exchange, firstRoutingKey, message);
        log.info("✅ Message sent to first queue successfully");
    }

    /**
     * Send message to the second queue
     */
    public void sendToSecondQueue(String message) {
        log.info("📤 Sending message to second queue: {}", message);
        rabbitTemplate.convertAndSend(exchange, secondRoutingKey, message);
        log.info("✅ Message sent to second queue successfully");
    }
}
