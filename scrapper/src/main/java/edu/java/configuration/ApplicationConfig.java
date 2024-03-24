package edu.java.configuration;

import edu.java.client.bot.BotClient;
import edu.java.client.github.GitHubClient;
import edu.java.client.stackoverflow.StackOverflowClient;
import edu.java.domain.link.dto.ServiceType;
import edu.java.domain.link.service.LinkChecker;
import edu.java.domain.link.service.LinkParser;
import edu.java.domain.link.service.LinkService;
import edu.java.domain.link.service.LinkUpdaterScheduler;
import edu.java.domain.link.service.github.GitHubLinkChecker;
import edu.java.domain.link.service.github.GithubLinkParser;
import edu.java.domain.link.service.stackoverflow.StackOverflowLinkChecker;
import edu.java.domain.link.service.stackoverflow.StackOverflowLinkParser;
import jakarta.validation.constraints.NotNull;
import java.time.Duration;
import java.util.HashMap;
import java.util.Map;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.context.annotation.Bean;
import org.springframework.validation.annotation.Validated;

@Validated
@ConfigurationProperties(prefix = "app", ignoreUnknownFields = false)
public record ApplicationConfig(
    @NotNull
    Scheduler scheduler,
    @NotNull
    AccessType databaseAccessType
) {
    @Bean
    @ConditionalOnProperty(prefix = "app", name = "scheduler.enable", havingValue = "true")
    public LinkUpdaterScheduler linkUpdateScheduler(LinkService linkService, BotClient botClient) {
        return new LinkUpdaterScheduler(linkService, botClient);
    }

    @Bean
    public LinkParser linkParser() {
        return LinkParser.link(
            new GithubLinkParser(),
            new StackOverflowLinkParser()
        );
    }

    @Bean
    public Map<ServiceType, LinkChecker> linkCheckerDict(
            GitHubClient gitHubClient,
            StackOverflowClient stackOverflowClient
    ) {
        var linkCheckerDict = new HashMap<ServiceType, LinkChecker>();

        linkCheckerDict.put(ServiceType.GitHub, new GitHubLinkChecker(gitHubClient));
        linkCheckerDict.put(ServiceType.StackOverflow, new StackOverflowLinkChecker(stackOverflowClient));

        return linkCheckerDict;
    }

    public record Scheduler(boolean enable, @NotNull Duration interval, @NotNull Duration forceCheckDelay) {
    }

    public enum AccessType {
        JDBC, JPA
    }
}
