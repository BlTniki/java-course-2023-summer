package edu.java.configuration;

import edu.java.LinkUpdaterScheduler;
import edu.java.client.github.GitHubClient;
import edu.java.client.stackoverflow.StackOverflowClient;
import edu.java.service.link.LinkChecker;
import edu.java.service.link.LinkParser;
import edu.java.service.link.github.GitHubLinkChecker;
import edu.java.service.link.github.GithubLinkParser;
import edu.java.service.link.model.ServiceType;
import edu.java.service.link.stackoverflow.StackOverflowLinkChecker;
import edu.java.service.link.stackoverflow.StackOverflowLinkParser;
import jakarta.validation.constraints.NotNull;
import java.time.Duration;
import java.util.HashMap;
import java.util.Map;
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
}
