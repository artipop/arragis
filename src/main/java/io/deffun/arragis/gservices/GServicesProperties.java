package io.deffun.arragis.gservices;

import org.springframework.boot.context.properties.ConfigurationProperties;

@ConfigurationProperties("google")
public record GServicesProperties(String applicationName) {
}
