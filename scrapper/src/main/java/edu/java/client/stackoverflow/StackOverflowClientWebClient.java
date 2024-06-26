package edu.java.client.stackoverflow;

import edu.java.client.exception.ClientException;
import edu.java.client.stackoverflow.model.QuestionsResponse;
import java.util.Collection;
import java.util.stream.Collectors;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.springframework.http.HttpStatusCode;
import org.springframework.web.client.HttpClientErrorException;
import org.springframework.web.reactive.function.client.WebClient;
import reactor.util.retry.Retry;

public class StackOverflowClientWebClient implements StackOverflowClient {
    private static final Logger LOGGER = LogManager.getLogger();
    private final WebClient webClient;
    private final Retry retry;

    public StackOverflowClientWebClient(WebClient.Builder webClientBuilder,  Retry retry) {
        this.webClient = webClientBuilder.build();
        this.retry = retry;
    }

    @Override
    public QuestionsResponse fetchQuestions(Collection<Long> questionIds) throws ClientException {
        try {
            return webClient
                .get()
                .uri(uriBuilder -> uriBuilder
                    .path("/questions/{ids}")
                    .queryParam("site", "stackoverflow")
                    .build(questionIds.stream()
                        .map(Object::toString)
                        .collect(Collectors.joining(";"))
                    )
                )
                .retrieve()
                .onStatus(HttpStatusCode::isError, response -> response.createException()
                    .flatMap(error -> {
                        throw new HttpClientErrorException(response.statusCode(), error.getResponseBodyAsString());
                    })
                )
                .bodyToMono(QuestionsResponse.class)
                .retryWhen(retry)
                .block();
        } catch (HttpClientErrorException e) {
            LOGGER.error("Got an error from API: " + e);
            throw ClientException.wrapException(e);
        }
    }
}
