package edu.java.client.stackoverflow;

import edu.java.client.exception.ClientException;
import edu.java.client.stackoverflow.model.QuestionsResponse;
import java.util.Collection;
import java.util.stream.Collectors;
import org.apache.logging.log4j.Logger;
import org.springframework.http.HttpStatusCode;
import org.springframework.web.client.HttpClientErrorException;
import org.springframework.web.reactive.function.client.WebClient;

public class StackOverflowClientWebClient implements StackOverflowClient {
    private final Logger logger;
    private final WebClient webClient;

    public StackOverflowClientWebClient(WebClient.Builder webClientBuilder, Logger logger) {
        this.webClient = webClientBuilder.build();
        this.logger = logger;
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
                .onStatus(HttpStatusCode::isError, response -> response.bodyToMono(String.class)
                    .flatMap(errorBody -> {
                        throw new HttpClientErrorException(response.statusCode(), errorBody);
                    }))
                .bodyToMono(QuestionsResponse.class)
                .block();
        } catch (HttpClientErrorException e) {
            logger.error("Got an error from API: " + e);
            throw ClientException.wrapException(e);
        }
    }
}
