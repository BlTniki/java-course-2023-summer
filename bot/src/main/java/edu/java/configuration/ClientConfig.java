package edu.java.configuration;

import edu.java.client.scrapper.ScrapperClient;
import edu.java.client.scrapper.ScrapperClientWebClient;
import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.NotNull;
import org.apache.logging.log4j.Logger;
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
    public ScrapperClient scrapperClient(WebClient.Builder builder, Logger logger) {
        builder.baseUrl(scrapper().baseUrl());
        return new ScrapperClientWebClient(builder, logger);
    }

    record Scrapper(@NotEmpty String baseUrl) {}
}
