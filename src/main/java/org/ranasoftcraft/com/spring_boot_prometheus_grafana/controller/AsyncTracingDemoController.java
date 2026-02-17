package org.ranasoftcraft.com.spring_boot_prometheus_grafana.controller;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.client.RestClient;

import java.util.HashMap;
import java.util.Map;
import java.util.Random;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.ExecutorService;

/**
 * This controller demonstrates distributed tracing capabilities
 */
@Slf4j
//@RestController
//@RequestMapping("/api/async")
@RequiredArgsConstructor
public class AsyncTracingDemoController {

    private final RestClient restClient;
    private final Random random = new Random();
    private final ExecutorService traceableExecutorService;

    @GetMapping("/trace-demo")
    public Map<String, Object> traceDemo() {
        log.info("Starting trace demo request");
        
        // Make multiple API calls to show in the trace - using our trace-aware executor service
        CompletableFuture<Map<String, Object>> serviceA = CompletableFuture.supplyAsync(this::callServiceA, traceableExecutorService);
        CompletableFuture<Map<String, Object>> serviceB = CompletableFuture.supplyAsync(this::callServiceB, traceableExecutorService);
        
        // Wait for both to complete
        CompletableFuture.allOf(serviceA, serviceB).join();
        
        // Combine results
        Map<String, Object> result = new HashMap<>();
        result.put("message", "Trace demo completed");
        result.put("serviceA", serviceA.join());
        result.put("serviceB", serviceB.join());
        
        log.info("Completed trace demo request");
        return result;
    }
    
    @GetMapping("/service-a")
    public Map<String, Object> serviceA() {
        log.info("Service A called");
        simulateWork(200);
        
        // Call nested service
        Map<String, Object> nestedResult = callNestedService("A");
        
        Map<String, Object> result = new HashMap<>();
        result.put("service", "A");
        result.put("timestamp", System.currentTimeMillis());
        result.put("nestedCall", nestedResult);
        return result;
    }
    
    @GetMapping("/service-b")
    public Map<String, Object> serviceB() {
        log.info("Service B called");
        simulateWork(300);
        
        // Call nested service
        Map<String, Object> nestedResult = callNestedService("B");
        
        Map<String, Object> result = new HashMap<>();
        result.put("service", "B");
        result.put("timestamp", System.currentTimeMillis());
        result.put("nestedCall", nestedResult);
        return result;
    }
    
    @GetMapping("/nested/{parent}")
    public Map<String, Object> nestedService(@PathVariable String parent) {
        log.info("Nested service called by parent: {}", parent);
        simulateWork(150);
        
        Map<String, Object> result = new HashMap<>();
        result.put("calledBy", parent);
        result.put("timestamp", System.currentTimeMillis());
        return result;
    }
    
    private Map<String, Object> callServiceA() {
        try {
            // Use relative URI to work in any environment
            return restClient
                    .get()
                    .uri("/api/service-a")
                    .retrieve()
                    .body(Map.class);
        } catch (Exception e) {
            log.error("Error calling Service A", e);
            Map<String, Object> error = new HashMap<>();
            error.put("error", e.getMessage());
            return error;
        }
    }
    
    private Map<String, Object> callServiceB() {
        try {
            // Use relative URI to work in any environment
            return restClient
                    .get()
                    .uri("/api/service-b")
                    .retrieve()
                    .body(Map.class);
        } catch (Exception e) {
            log.error("Error calling Service B", e);
            Map<String, Object> error = new HashMap<>();
            error.put("error", e.getMessage());
            return error;
        }
    }
    
    private Map<String, Object> callNestedService(String parent) {
        try {
            // Use relative URI to work in any environment
            return restClient
                    .get()
                    .uri("/api/nested/" + parent)
                    .retrieve()
                    .body(Map.class);
        } catch (Exception e) {
            log.error("Error calling Nested Service", e);
            Map<String, Object> error = new HashMap<>();
            error.put("error", e.getMessage());
            return error;
        }
    }
    
    private void simulateWork(int maxMillis) {
        try {
            // Random delay to simulate work
            Thread.sleep(random.nextInt(maxMillis));
        } catch (InterruptedException e) {
            Thread.currentThread().interrupt();
        }
    }
}
