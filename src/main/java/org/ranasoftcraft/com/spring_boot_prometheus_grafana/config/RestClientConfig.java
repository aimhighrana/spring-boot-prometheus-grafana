package org.ranasoftcraft.com.spring_boot_prometheus_grafana.config;

import io.micrometer.observation.ObservationRegistry;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Primary;
import org.springframework.web.client.RestClient;

@Configuration
public class RestClientConfig {

    /**
     * Create a RestClient that propagates trace headers
     */
    @Bean
    @Primary
    public RestClient traceAwareRestClient(ObservationRegistry observationRegistry) {
        return RestClient.builder()
                .baseUrl("http://localhost:8888")
                .observationRegistry(observationRegistry)
                .build();
    }
}
