package edu.java.configuration;

import edu.java.client.bot.BotClient;
import edu.java.client.github.GitHubClient;
import edu.java.client.stackoverflow.StackOverflowClient;
import edu.java.controller.filter.RateFilter;
import edu.java.controller.limiter.RateLimiterService;
import edu.java.domain.link.dto.ServiceType;
import edu.java.domain.link.service.LinkChecker;
import edu.java.domain.link.service.LinkParser;
import edu.java.domain.link.service.LinkService;
import edu.java.domain.link.service.LinkUpdaterScheduler;
import edu.java.domain.link.service.github.GitHubLinkChecker;
import edu.java.domain.link.service.github.GithubLinkParser;
import edu.java.domain.link.service.stackoverflow.StackOverflowLinkChecker;
import edu.java.domain.link.service.stackoverflow.StackOverflowLinkParser;
import io.github.bucket4j.Bandwidth;
import jakarta.validation.constraints.NotNull;
import java.time.Duration;
import java.time.format.DateTimeParseException;
import java.util.HashMap;
import java.util.Map;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.boot.web.servlet.FilterRegistrationBean;
import org.springframework.context.annotation.Bean;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.servlet.HandlerExceptionResolver;

@Validated
@ConfigurationProperties(prefix = "app", ignoreUnknownFields = false)
public record ApplicationConfig(
    @NotNull
    Scheduler scheduler,
    @NotNull
    AccessType databaseAccessType,
    RateLimiting rateLimit
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

    @Bean
    @ConditionalOnProperty(prefix = "app", name = "rate-limit.enable", havingValue = "true")
    public RateLimiterService rateLimiterService() {
        // validate .yml
        if (rateLimit.capacity() == null) {
            throw new IllegalArgumentException("app.rate-limit.capacity: The capacity must be specified (long)");
        }
        if (rateLimit.relief() == null) {
            throw new IllegalArgumentException(
                "app.rate-limit.relief: The relief must be specified (relief.tokens, relief.period in ISO 8601)"
            );
        }
        if (rateLimit.relief().tokens() == null) {
            throw new IllegalArgumentException(
                "app.rate-limit.relief.tokens: The tokens must be specified (long)"
            );
        }
        if (rateLimit.relief().period() == null) {
            throw new IllegalArgumentException(
                "app.rate-limit.relief.period: The period must be specified (String in ISO 8601)"
            );
        }
        Duration period;
        try {
            period = Duration.parse(rateLimit.relief().period());
        } catch (DateTimeParseException e) {
            throw new IllegalArgumentException(
                "app.rate-limit.relief.period: The period must match ISO 8601 format",
                e
            );
        }

        Bandwidth bandwidth = Bandwidth.builder()
            .capacity(rateLimit.capacity())
            .refillIntervally(rateLimit.relief().tokens(), period)
            .build();
        return new RateLimiterService(bandwidth);
    }

    @Bean
    @ConditionalOnProperty(prefix = "app", name = "rate-limit.enable", havingValue = "true")
    public FilterRegistrationBean<RateFilter> rateLimitFilter(
        RateLimiterService rateLimiterService,
        HandlerExceptionResolver handlerExceptionResolver
    ) {
        FilterRegistrationBean<RateFilter> registrationBean = new FilterRegistrationBean<>();

        registrationBean.setFilter(new RateFilter(rateLimiterService, handlerExceptionResolver));
        registrationBean.setOrder(1);

        return registrationBean;
    }

    public record Scheduler(boolean enable, @NotNull Duration interval, @NotNull Duration forceCheckDelay) {
    }

    @SuppressWarnings("unused")
    public enum AccessType {
        JDBC, JPA
    }

    public record RateLimiting(
        Boolean enable,
        Long capacity,
        Relief relief
    ) {}

    public record Relief(
        Long tokens,
        String period // ISO 8601
    ) {}
}
