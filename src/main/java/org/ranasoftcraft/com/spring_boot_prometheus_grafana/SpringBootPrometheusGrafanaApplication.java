package org.ranasoftcraft.com.spring_boot_prometheus_grafana;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.EnableAspectJAutoProxy;
import org.springframework.scheduling.annotation.EnableScheduling;

@SpringBootApplication
@EnableAspectJAutoProxy(proxyTargetClass = true)
@EnableScheduling
public class SpringBootPrometheusGrafanaApplication {

	public static void main(String[] args) {
		SpringApplication.run(SpringBootPrometheusGrafanaApplication.class, args);


//		java.util.concurrent.TimeUnit.SECONDS.sleep(10);
	}

}
