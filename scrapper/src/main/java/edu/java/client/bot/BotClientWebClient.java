package edu.java.client.bot;

import edu.java.client.bot.model.LinkUpdate;
import edu.java.client.exception.ClientException;
import jakarta.validation.constraints.NotNull;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.springframework.http.HttpStatusCode;
import org.springframework.web.client.HttpClientErrorException;
import org.springframework.web.reactive.function.client.WebClient;
import reactor.core.publisher.Mono;

public class BotClientWebClient implements BotClient {
    private static final Logger LOGGER = LogManager.getLogger();
    private final WebClient webClient;

    public BotClientWebClient(WebClient.Builder webClientBuilder) {
        this.webClient = webClientBuilder.build();
    }

    @Override
    public void sendLinkUpdate(@NotNull LinkUpdate linkUpdate) throws ClientException {
        try {
            webClient
                .post()
                .uri("/updates")
                .body(Mono.just(linkUpdate), LinkUpdate.class)
                .retrieve()
                .onStatus(HttpStatusCode::isError, response -> response.createException()
                    .flatMap(error -> {
                        throw new HttpClientErrorException(response.statusCode(), error.getResponseBodyAsString());
                    })
                )
                .bodyToMono(String.class)
                .block();
        } catch (HttpClientErrorException e) {
            LOGGER.error(e);
            throw ClientException.wrapException(e);
        }
    }
}
