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
import reactor.util.retry.Retry;

public class BotClientWebClient implements BotClient {
    private static final Logger LOGGER = LogManager.getLogger();
    private final WebClient webClient;
    private final Retry retry;

    public BotClientWebClient(WebClient.Builder webClientBuilder, Retry retry) {
        this.webClient = webClientBuilder.build();
        this.retry = retry;
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
                .retryWhen(retry)
                .block();
        } catch (HttpClientErrorException e) {
            LOGGER.error(e);
            throw ClientException.wrapException(e);
        }
    }
}
