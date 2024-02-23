package edu.java.configuration;

import edu.java.LinkUpdaterScheduler;
import jakarta.validation.constraints.NotNull;
import java.time.Duration;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.context.annotation.Bean;
import org.springframework.validation.annotation.Validated;

@Validated
@ConfigurationProperties(prefix = "app", ignoreUnknownFields = false)
public record ApplicationConfig(
    @NotNull
    Scheduler scheduler
) {
    @Bean
    public Logger logger() {
        return LogManager.getLogger("app");
    }

    @Bean
    public LinkUpdaterScheduler linkUpdateScheduler(Logger logger) {
        return new LinkUpdaterScheduler(logger);
    }

    public record Scheduler(boolean enable, @NotNull Duration interval, @NotNull Duration forceCheckDelay) {
    }
}
