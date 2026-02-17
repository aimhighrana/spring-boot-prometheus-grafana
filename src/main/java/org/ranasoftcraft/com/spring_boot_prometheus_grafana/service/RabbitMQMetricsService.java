package org.ranasoftcraft.com.spring_boot_prometheus_grafana.service;

import io.micrometer.core.instrument.Counter;
import io.micrometer.core.instrument.MeterRegistry;
import io.micrometer.core.instrument.Timer;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.util.concurrent.TimeUnit;

@Slf4j
@Service
public class RabbitMQMetricsService {

    private final MeterRegistry meterRegistry;

    public RabbitMQMetricsService(MeterRegistry meterRegistry) {
        this.meterRegistry = meterRegistry;
    }

    /**
     * Record message processing time and count
     * 
     * @param queueName The queue name (e.g., "cr_queue", "bulk_queue")
     * @param tenant The tenant ID from message header
     * @param processingTimeMs Processing time in milliseconds
     * @param success Whether processing was successful
     */
    public void recordMessageProcessing(String queueName, String tenant, long processingTimeMs, boolean success) {
        String tenantValue = (tenant != null && !tenant.isEmpty()) ? tenant : "unknown";
        String status = success ? "success" : "failure";

        // Record processing duration using Timer with histogram buckets for percentiles
        Timer.builder("rabbitmq.consumer.processing.duration")
                .description("Time taken to process RabbitMQ messages")
                .tags("queue", queueName, "tenant", tenantValue, "status", status)
                .publishPercentileHistogram()  // Enable histogram buckets
                .serviceLevelObjectives(
                        java.time.Duration.ofMillis(100),
                        java.time.Duration.ofMillis(500),
                        java.time.Duration.ofSeconds(1),
                        java.time.Duration.ofSeconds(2),
                        java.time.Duration.ofSeconds(5),
                        java.time.Duration.ofSeconds(10)
                )
                .register(meterRegistry)
                .record(processingTimeMs, TimeUnit.MILLISECONDS);

        // Increment message counter
        Counter.builder("rabbitmq.consumer.messages.processed")
                .description("Total number of RabbitMQ messages processed")
                .tags("queue", queueName, "tenant", tenantValue, "status", status)
                .register(meterRegistry)
                .increment();

        log.debug("Recorded metrics - Queue: {}, Tenant: {}, Duration: {}ms, Status: {}", 
                queueName, tenantValue, processingTimeMs, status);
    }

    /**
     * Record only failures (for exceptions during processing)
     * 
     * @param queueName The queue name
     * @param tenant The tenant ID from message header
     * @param exception The exception that occurred
     */
    public void recordProcessingError(String queueName, String tenant, Exception exception) {
        String tenantValue = (tenant != null && !tenant.isEmpty()) ? tenant : "unknown";

        Counter.builder("rabbitmq.consumer.errors")
                .description("Total number of RabbitMQ processing errors")
                .tags("queue", queueName, "tenant", tenantValue, "error", exception.getClass().getSimpleName())
                .register(meterRegistry)
                .increment();

        log.error("Recorded error metric - Queue: {}, Tenant: {}, Error: {}", 
                queueName, tenantValue, exception.getMessage());
    }
}
