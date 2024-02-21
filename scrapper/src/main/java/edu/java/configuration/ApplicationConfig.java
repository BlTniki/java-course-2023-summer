package edu.java.configuration;

import edu.java.client.github.GitHubClient;
import edu.java.client.github.GitHubClientRestClient;
import jakarta.validation.constraints.NotEmpty;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.context.annotation.Bean;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.client.RestClient;

@Validated
@ConfigurationProperties(prefix = "app", ignoreUnknownFields = false)
public record ApplicationConfig(
    @NotEmpty String url
) {
    @Bean
    public GitHubClient gitHubClient(RestClient.Builder builder) {
        System.out.println("url: " + url);
        var c = new GitHubClientRestClient(builder);
        return c;
    }
//    public record Scheduler(boolean enable, @NotNull Duration interval, @NotNull Duration forceCheckDelay) {
//    }
}
