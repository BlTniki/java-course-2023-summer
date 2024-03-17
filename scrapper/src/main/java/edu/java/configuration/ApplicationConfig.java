package edu.java.configuration;

import edu.java.LinkUpdaterScheduler;
import edu.java.service.link.LinkParser;
import edu.java.service.link.github.GithubLinkParser;
import edu.java.service.link.stackoverflow.StackOverflowLinkParser;
import jakarta.validation.constraints.NotNull;
import java.time.Duration;
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
    public LinkUpdaterScheduler linkUpdateScheduler() {
        return new LinkUpdaterScheduler();
    }

    @Bean
    public LinkParser linkParser() {
        return LinkParser.link(
            new GithubLinkParser(),
            new StackOverflowLinkParser()
        );
    }

    public record Scheduler(boolean enable, @NotNull Duration interval, @NotNull Duration forceCheckDelay) {
    }
}
