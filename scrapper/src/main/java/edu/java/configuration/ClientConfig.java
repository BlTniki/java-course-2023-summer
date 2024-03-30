package edu.java.configuration;

import edu.java.client.WebClientRetryUtils;
import edu.java.client.bot.BotClient;
import edu.java.client.bot.BotClientWebClient;
import edu.java.client.github.GitHubClient;
import edu.java.client.github.GitHubClientWebClient;
import edu.java.client.stackoverflow.StackOverflowClient;
import edu.java.client.stackoverflow.StackOverflowClientWebClient;
import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.NotNull;
import java.util.Set;
import java.util.function.Predicate;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.context.annotation.Bean;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.reactive.function.client.WebClient;
import reactor.util.retry.Retry;

@SuppressWarnings("MultipleStringLiterals")
@Validated
@ConfigurationProperties(prefix = "client", ignoreUnknownFields = false)
public record ClientConfig(
    @NotNull GitHub gitHub,
    @NotNull StackOverflow stackOverflow,
    @NotNull Bot bot,
    RetryConfig retry
) {
    public static final int DEFAULT_MAX_ATTEMPTS = 100;
    public static final int DEFAULT_BASE_DELAY_MS = 1000;

    @Bean
    public GitHubClient gitHubClient(WebClient.Builder builder) {
        builder.baseUrl(
            gitHub.baseUrl == null ? "https://api.github.com" : gitHub.baseUrl()
        );
        builder.defaultHeader("Authorization", "Bearer " + gitHub.token);
        builder.defaultHeader("Accept", "application/vnd.github+json");
        return new GitHubClientWebClient(builder, buildRetry());
    }

    @Bean
    public StackOverflowClient stackOverflowClient(WebClient.Builder builder) {
        builder.baseUrl(
            stackOverflow.baseUrl == null ? "https://api.stackexchange.com" : gitHub.baseUrl()
        );
        builder.defaultHeader("Accept", "application/json");
        return new StackOverflowClientWebClient(builder, buildRetry());
    }

    @Bean
    public BotClient botClient(WebClient.Builder builder) {
        builder.baseUrl(bot().baseUrl());
        builder.defaultHeader("Accept", "application/json");
        return new BotClientWebClient(builder, buildRetry());
    }

    private Retry buildRetry() {
        if (retry == null || !retry.enable()) {
            return Retry.max(0);
        }


        final int maxAttempts = retry.maxAttempts() == 0 ? DEFAULT_MAX_ATTEMPTS : retry().maxAttempts();
        final int baseDelayMs = retry.baseDelayMs() == 0 ? DEFAULT_BASE_DELAY_MS : retry().baseDelayMs();
        final Predicate<Throwable> predicate = WebClientRetryUtils.buildFilter(retry.retryOnCodes());

        switch (retry.type()) {
            case constant -> {
                return WebClientRetryUtils.buildConstantRetry(maxAttempts, baseDelayMs, predicate);
            }
            case linear -> {
                return WebClientRetryUtils.buildLinearRetry(maxAttempts, baseDelayMs, predicate);
            }
            case exponential -> {
                return WebClientRetryUtils.buildExponentialRetry(maxAttempts, baseDelayMs, predicate);
            }
            case null, default -> throw new IllegalArgumentException(
                "The retry type must be specified (constant, linear, exponential)"
            );
        }
    }

    public record GitHub(@NotEmpty String token, String baseUrl) {}

    public record StackOverflow(String baseUrl) {}

    public record Bot(@NotEmpty String baseUrl) {}

    public record RetryConfig(
        boolean enable,
        RetryType type,
        int maxAttempts, // default 100
        int baseDelayMs, // default 1000 ms
        Set<Integer> retryOnCodes // default any non 2xx
    ) {}

    public enum RetryType {
        constant, linear, exponential
    }
}
