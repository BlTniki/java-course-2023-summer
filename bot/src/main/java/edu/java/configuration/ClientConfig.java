package edu.java.configuration;

import edu.java.client.WebClientRetryUtils;
import edu.java.client.scrapper.ScrapperClient;
import edu.java.client.scrapper.ScrapperClientWebClient;
import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.NotNull;
import java.util.Set;
import java.util.function.Predicate;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.context.annotation.Bean;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.reactive.function.client.WebClient;
import reactor.util.retry.Retry;

@Validated
@ConfigurationProperties(prefix = "client", ignoreUnknownFields = false)
public record ClientConfig(
    @NotNull Scrapper scrapper,
    RetryConfig retry
) {
    public static final int DEFAULT_MAX_ATTEMPTS = 100;
    public static final int DEFAULT_BASE_DELAY_MS = 1000;

    @Bean
    public ScrapperClient scrapperClient(WebClient.Builder builder) {
        builder.baseUrl(scrapper().baseUrl());
        return new ScrapperClientWebClient(builder, buildRetry());
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

    record Scrapper(@NotEmpty String baseUrl) {}

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
