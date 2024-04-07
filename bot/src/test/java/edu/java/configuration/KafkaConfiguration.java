package edu.java.configuration;

import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.validation.annotation.Validated;

@Validated
@ConfigurationProperties(prefix = "kafka")
public record KafkaConfiguration(
    Consumer consumer
) {
    public record Consumer(Topic topic) {}
    public record Topic(String name) {}
}
