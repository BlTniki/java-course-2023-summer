package edu.java.configuration;

import edu.java.client.scrapper.ScrapperClient;
import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.NotNull;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.context.annotation.Bean;
import org.springframework.validation.annotation.Validated;
import static org.mockito.Mockito.mock;

@Validated
@ConfigurationProperties(prefix = "client", ignoreUnknownFields = false)
public record ClientConfig(
    @NotNull Scrapper scrapper
) {
    @Bean
    public ScrapperClient scrapperClient() {
        return mock(ScrapperClient.class);
    }
    record Scrapper(@NotEmpty String baseUrl) {}
}
