package io.deffun.arragis;

import io.deffun.arragis.gservices.GServicesProperties;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.context.properties.EnableConfigurationProperties;

@SpringBootApplication
@EnableConfigurationProperties(GServicesProperties.class)
public class ArragisApplication {

    public static void main(String[] args) {
        SpringApplication.run(ArragisApplication.class, args);
    }

}
