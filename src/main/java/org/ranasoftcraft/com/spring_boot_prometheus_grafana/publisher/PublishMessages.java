package org.ranasoftcraft.com.spring_boot_prometheus_grafana.publisher;

import lombok.RequiredArgsConstructor;
import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Configuration;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;
import org.springframework.stereotype.Service;

import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import java.util.concurrent.ThreadLocalRandom;

@Configuration
@RequiredArgsConstructor
public class PublishMessages {

    private final RabbitTemplate template;


    @Value("${rabbitmq.exchange}")
    private String exchange;

    @Value("${rabbitmq.routing-key.first}")
    private String firstRoutingKey;

    @Value("${rabbitmq.routing-key.second}")
    private String secondRoutingKey;

    private final List<String> tenants = Arrays.asList("72838", "38432");

    @Scheduled(fixedDelay = 5000)
    public void sendFirstMessages() {
        final String sendToMe = tenants.stream()
                .skip(ThreadLocalRandom.current().nextInt(tenants.size()))
                .findFirst()
                .orElse(null);

        var data = Collections.singletonMap("data", """
                Holla, i am inn... welcome to my ways .. 
                
                """);

        template.convertAndSend(exchange, firstRoutingKey, data, (m)-> {
            m.getMessageProperties().setHeader("tenant", sendToMe);
            return m;
        });
    }


    @Scheduled(fixedDelay = 5000)
    public void sendSecondMessage() {
        final String sendToMe = tenants.stream()
                .skip(ThreadLocalRandom.current().nextInt(tenants.size()))
                .findFirst()
                .orElse(null);

        var data = Collections.singletonMap("data", """
                Holla, i am inn... welcome to my another ways ... 
                
                """);

        template.convertAndSend(exchange, secondRoutingKey, data, (m)-> {
            m.getMessageProperties().setHeader("tenant", sendToMe);
            return m;
        });
    }
}
