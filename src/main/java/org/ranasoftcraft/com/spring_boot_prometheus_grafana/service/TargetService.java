package org.ranasoftcraft.com.spring_boot_prometheus_grafana.service;

import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestClient;

import java.util.Map;

/**
 * @author sandeep.rana
 */
@Service @RequiredArgsConstructor
public class TargetService {

    private final RestClient restClient;

    public Map<String,?> ruleHealth() {
        return restClient.
                get()
                .uri("/rule/actuator/health/liveness")
                .retrieve().body(Map.class);
    }


}
