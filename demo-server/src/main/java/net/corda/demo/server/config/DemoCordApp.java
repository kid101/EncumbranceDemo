package net.corda.demo.server.config;

import net.corda.demo.server.service.ServiceScheduler;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.context.annotation.Bean;
import org.springframework.scheduling.annotation.EnableScheduling;

@SpringBootApplication(scanBasePackages = "net.corda.demo.server.*")
@EnableScheduling
public class DemoCordApp {
    public static void main(String[] args) {
        SpringApplication.run(DemoCordApp.class, args);
    }
/*
     comment below piece of code, if using boot version 2.0 and above. Uncomment, if using version below 2.0.

    @Bean
    public TomcatEmbeddedServletContainerFactory tomcatEmbeddedServletContainerFactory() {
        return new TomcatEmbeddedServletContainerFactory();
    }*/

    @Bean
    @ConditionalOnProperty(name = "active.service", havingValue = "true")
    public ServiceScheduler serviceScheduler() {
        return new ServiceScheduler();
    }


}
