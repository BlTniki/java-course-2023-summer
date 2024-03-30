package edu.java.configuration;

import edu.java.client.WebClientRetryUtils;
import edu.java.client.github.GitHubClient;
import edu.java.client.github.GitHubClientWebClient;
import edu.java.client.stackoverflow.StackOverflowClient;
import edu.java.client.stackoverflow.StackOverflowClientWebClient;
import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.NotNull;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Profile;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.reactive.function.client.WebClient;
import reactor.util.retry.Retry;
import java.util.Set;

@SuppressWarnings("MultipleStringLiterals")
@Validated
@ConfigurationProperties(prefix = "client", ignoreUnknownFields = false)
@Profile("test")
public record ClientConfig(
    @NotNull GitHub gitHub,
    @NotNull StackOverflow stackOverflow
) {
    private Retry buildRetry() {
        return WebClientRetryUtils.buildConstantRetry(
            1,
            0,
            WebClientRetryUtils.buildFilter(Set.of(420))
        );
    }
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
    public record GitHub(@NotEmpty String token, String baseUrl) {}

    public record StackOverflow(String baseUrl) {}
}
