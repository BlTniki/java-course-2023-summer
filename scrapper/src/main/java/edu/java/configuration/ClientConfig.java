package edu.java.configuration;

import edu.java.client.bot.BotClient;
import edu.java.client.bot.BotClientWebClient;
import edu.java.client.github.GitHubClient;
import edu.java.client.github.GitHubClientWebClient;
import edu.java.client.stackoverflow.StackOverflowClient;
import edu.java.client.stackoverflow.StackOverflowClientWebClient;
import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.NotNull;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.context.annotation.Bean;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.reactive.function.client.WebClient;

@SuppressWarnings("MultipleStringLiterals")
@Validated
@ConfigurationProperties(prefix = "client", ignoreUnknownFields = false)
public record ClientConfig(
    @NotNull GitHub gitHub,
    @NotNull StackOverflow stackOverflow,
    @NotNull Bot bot
) {
    @Bean
    public GitHubClient gitHubClient(WebClient.Builder builder) {
        builder.baseUrl(
            gitHub.baseUrl == null ? "https://api.github.com" : gitHub.baseUrl()
        );
        builder.defaultHeader("Authorization", "Bearer " + gitHub.token);
        builder.defaultHeader("Accept", "application/vnd.github+json");
        return new GitHubClientWebClient(builder);
    }

    @Bean
    public StackOverflowClient stackOverflowClient(WebClient.Builder builder) {
        builder.baseUrl(
            stackOverflow.baseUrl == null ? "https://api.stackexchange.com" : gitHub.baseUrl()
        );
        builder.defaultHeader("Accept", "application/json");
        return new StackOverflowClientWebClient(builder);
    }

    @Bean
    public BotClient botClient(WebClient.Builder builder) {
        builder.baseUrl(bot().baseUrl());
        builder.defaultHeader("Accept", "application/json");
        return new BotClientWebClient(builder);
    }

    public record GitHub(@NotEmpty String token, String baseUrl) {}

    public record StackOverflow(String baseUrl) {}

    public record Bot(@NotEmpty String baseUrl) {}
}
