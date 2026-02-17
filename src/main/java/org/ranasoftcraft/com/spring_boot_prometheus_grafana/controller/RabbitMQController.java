package org.ranasoftcraft.com.spring_boot_prometheus_grafana.controller;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.ranasoftcraft.com.spring_boot_prometheus_grafana.service.MessageProducerService;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.Map;

@Slf4j
@RestController
@RequestMapping("/api/rabbitmq")
@RequiredArgsConstructor
public class RabbitMQController {

    private final MessageProducerService messageProducerService;

    /**
     * Send message to the first queue
     * Example: POST /api/rabbitmq/send/first?message=Hello World
     */
    @PostMapping("/send/first")
    public ResponseEntity<Map<String, String>> sendToFirstQueue(
            @RequestParam(defaultValue = "Hello from First Queue!") String message) {
        try {
            messageProducerService.sendToFirstQueue(message);
            return ResponseEntity.ok(Map.of(
                    "status", "success",
                    "message", "Message sent to first queue",
                    "payload", message
            ));
        } catch (Exception e) {
            log.error("Error sending message to first queue", e);
            return ResponseEntity.internalServerError().body(Map.of(
                    "status", "error",
                    "message", e.getMessage()
            ));
        }
    }

    /**
     * Send message to the second queue
     * Example: POST /api/rabbitmq/send/second?message=Hello World
     */
    @PostMapping("/send/second")
    public ResponseEntity<Map<String, String>> sendToSecondQueue(
            @RequestParam(defaultValue = "Hello from Second Queue!") String message) {
        try {
            messageProducerService.sendToSecondQueue(message);
            return ResponseEntity.ok(Map.of(
                    "status", "success",
                    "message", "Message sent to second queue",
                    "payload", message
            ));
        } catch (Exception e) {
            log.error("Error sending message to second queue", e);
            return ResponseEntity.internalServerError().body(Map.of(
                    "status", "error",
                    "message", e.getMessage()
            ));
        }
    }

    /**
     * Send message to both queues
     * Example: POST /api/rabbitmq/send/both?message=Hello World
     */
    @PostMapping("/send/both")
    public ResponseEntity<Map<String, String>> sendToBothQueues(
            @RequestParam(defaultValue = "Hello from Both Queues!") String message) {
        try {
            messageProducerService.sendToFirstQueue(message);
            messageProducerService.sendToSecondQueue(message);
            return ResponseEntity.ok(Map.of(
                    "status", "success",
                    "message", "Message sent to both queues",
                    "payload", message
            ));
        } catch (Exception e) {
            log.error("Error sending message to both queues", e);
            return ResponseEntity.internalServerError().body(Map.of(
                    "status", "error",
                    "message", e.getMessage()
            ));
        }
    }
}
