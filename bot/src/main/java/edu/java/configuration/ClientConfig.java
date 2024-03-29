package edu.java.configuration;

import edu.java.client.scrapper.ScrapperClient;
import edu.java.client.scrapper.ScrapperClientWebClient;
import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.NotNull;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.context.annotation.Bean;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.reactive.function.client.WebClient;

@Validated
@ConfigurationProperties(prefix = "client", ignoreUnknownFields = false)
public record ClientConfig(
    @NotNull Scrapper scrapper
) {
    @Bean
    public ScrapperClient scrapperClient(WebClient.Builder builder) {
        builder.baseUrl(scrapper().baseUrl());
        return new ScrapperClientWebClient(builder);
    }

    record Scrapper(@NotEmpty String baseUrl) {}
}
