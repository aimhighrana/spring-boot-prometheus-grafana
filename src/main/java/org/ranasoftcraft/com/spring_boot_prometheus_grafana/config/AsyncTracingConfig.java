package org.ranasoftcraft.com.spring_boot_prometheus_grafana.config;
//
//import io.micrometer.context.ContextExecutorService;
//import io.micrometer.context.ContextRegistry;
//import io.micrometer.context.ContextSnapshotFactory;
//import io.micrometer.context.ThreadLocalAccessor;
//import io.micrometer.observation.ObservationRegistry;
//import io.micrometer.observation.contextpropagation.ObservationThreadLocalAccessor;
//import io.micrometer.tracing.Tracer;
//import io.micrometer.tracing.handler.PropagatingSenderTracingObservationHandler;
//import io.micrometer.tracing.propagation.Propagator;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.scheduling.annotation.EnableAsync;

import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

/**
 * Configuration for tracing across async boundaries
 */
//@Configuration
//@EnableAsync
public class AsyncTracingConfig {
    
//    /**
//     * Make sure ObservationRegistry has tracing capability
//     */
//    @Bean
//    public PropagatingSenderTracingObservationHandler<?> tracingObservationHandler(Tracer tracer, Propagator propagator) {
//        return new PropagatingSenderTracingObservationHandler<>(tracer, propagator);
//    }
//
//    /**
//     * Creates a thread pool that propagates the trace context
//     */
//    @Bean
//    public ExecutorService traceableExecutorService(ObservationRegistry observationRegistry) {
//        // Create a standard executor service
//        ExecutorService executorService = Executors.newVirtualThreadPerTaskExecutor();
//
//        // Register the observation thread local accessor
//        ContextRegistry contextRegistry = ContextRegistry.getInstance();
//        contextRegistry.registerThreadLocalAccessor(new ObservationThreadLocalAccessor(observationRegistry));
//
//        // Create a context snapshot factory
//        ContextSnapshotFactory contextSnapshotFactory = ContextSnapshotFactory.builder()
//                .contextRegistry(contextRegistry)
//                .build();
//
//        // Wrap it with context propagation capabilities
//        return ContextExecutorService.wrap(executorService, contextSnapshotFactory);
//    }
}
