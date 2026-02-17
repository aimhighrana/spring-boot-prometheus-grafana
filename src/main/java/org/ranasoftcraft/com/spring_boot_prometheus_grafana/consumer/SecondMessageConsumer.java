package org.ranasoftcraft.com.spring_boot_prometheus_grafana.consumer;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.ranasoftcraft.com.spring_boot_prometheus_grafana.service.RabbitMQMetricsService;
import org.springframework.amqp.core.Message;
import org.springframework.amqp.rabbit.annotation.RabbitListener;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import java.util.concurrent.ThreadLocalRandom;

@Slf4j
@Component
@RequiredArgsConstructor
public class SecondMessageConsumer {

    private final RabbitMQMetricsService metricsService;

    @Value("${rabbitmq.queue.second}")
    private String queueName;

    @RabbitListener(queues = "${rabbitmq.queue.second}")
    public void consumeMessage(Message message) {
        long startTime = System.currentTimeMillis();
        String tenant = null;
        boolean success = true;

        try {
            log.info("✅ [Second Consumer] Received message: {}", new String(message.getBody()));

            tenant = (String) message.getMessageProperties().getHeader("tenant");

            if (tenant != null && tenant.equals("38432")) {
                int rand = ThreadLocalRandom.current().nextInt(4000, 6001);

                try {
                    Thread.sleep(rand); // sorry i am slow
                } catch (InterruptedException e) {
                    success = false;
                    throw new RuntimeException(e);
                }
            }

            // Fire-and-forget pattern: just log and acknowledge
            // No further processing or response needed
        } catch (Exception e) {
            success = false;
            metricsService.recordProcessingError(queueName, tenant, e);
            log.error("Error processing message from queue: {}, tenant: {}", queueName, tenant, e);
            throw e;
        } finally {
            long processingTime = System.currentTimeMillis() - startTime;
            metricsService.recordMessageProcessing(queueName, tenant, processingTime, success);
        }
    }
}
