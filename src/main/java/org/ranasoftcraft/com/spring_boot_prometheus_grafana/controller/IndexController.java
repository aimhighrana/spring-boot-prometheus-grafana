package org.ranasoftcraft.com.spring_boot_prometheus_grafana.controller;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.ranasoftcraft.com.spring_boot_prometheus_grafana.service.TargetService;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;

@Slf4j
@Controller @RequiredArgsConstructor
public class IndexController {

    private final TargetService targetService;

    @GetMapping("/")
    public String index() {

        var health  = targetService.ruleHealth();
        log.info("health = {}", health);
        return "index";
    }
}
