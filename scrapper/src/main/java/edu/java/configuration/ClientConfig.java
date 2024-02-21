package edu.java.configuration;

import edu.java.client.github.GitHubClient;
import edu.java.client.github.GitHubClientRestClient;
import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.NotNull;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.context.annotation.Bean;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.client.RestClient;

@Validated
@ConfigurationProperties(prefix = "client", ignoreUnknownFields = false)
public record ClientConfig(

    @NotNull GitHub gitHub
) {
    @Bean
    public GitHubClient gitHubClient(RestClient.Builder builder) {
        System.out.println("roken: " + gitHub.token());
        return new GitHubClientRestClient(builder);
    }

    public record GitHub(@NotEmpty String token, @NotEmpty String baseUrl) {
    }
}
