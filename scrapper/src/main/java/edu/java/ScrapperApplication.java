package edu.java;

import edu.java.configuration.ApplicationConfig;
import edu.java.configuration.ClientConfig;
import edu.java.configuration.KafkaConfiguration;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.autoconfigure.kafka.KafkaAutoConfiguration;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.scheduling.annotation.EnableScheduling;

@SpringBootApplication(exclude = KafkaAutoConfiguration.class)
@EnableConfigurationProperties({ApplicationConfig.class, ClientConfig.class, KafkaConfiguration.class})
@EnableScheduling
public class ScrapperApplication {
    public static void main(String[] args) {
        SpringApplication.run(ScrapperApplication.class, args);
    }
}
