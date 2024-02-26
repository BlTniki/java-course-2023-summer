package edu.java.client.scrapper;

import edu.java.client.exception.ClientException;
import edu.java.client.scrapper.exception.ErrorResponseException;
import edu.java.client.scrapper.exception.ScrapperClientException;
import edu.java.client.scrapper.model.AddLinkRequest;
import edu.java.client.scrapper.model.ErrorResponse;
import edu.java.client.scrapper.model.LinkResponse;
import edu.java.client.scrapper.model.ListLinksResponse;
import java.net.URI;
import org.apache.logging.log4j.Logger;
import org.springframework.http.HttpMethod;
import org.springframework.http.HttpStatusCode;
import org.springframework.web.reactive.function.client.WebClient;
import reactor.core.publisher.Mono;

public class ScrapperClientWebClient implements ScrapperClient {
    public static final String TG_ID_PATH = "/tg-chat/{id}";
    public static final String TG_CHAT_ID_HEADER = "Tg-Chat-Id";
    public static final String LINKS_PATH = "/links";
    private final Logger logger;
    private final WebClient webClient;

    public ScrapperClientWebClient(WebClient.Builder webClientBuilder, Logger logger) {
        this.webClient = webClientBuilder.build();
        this.logger = logger;
    }

    @Override
    public void registerChat(long tgChatId) throws ClientException {
        try {
            webClient
                .get()
                .uri(uriBuilder -> uriBuilder.path(TG_ID_PATH)
                    .build(tgChatId)
                )
                .retrieve()
                .onStatus(HttpStatusCode::isError, response -> response.createException()
                    .flatMap(error -> {
                        throw new ErrorResponseException(
                            response.statusCode(), error.getResponseBodyAs(ErrorResponse.class)
                        );
                    })
                ).bodyToMono(String.class)
                .block();
        } catch (ErrorResponseException e) {
            logger.error(e);
            throw ScrapperClientException.wrapException(e);
        }
    }

    @Override
    public void deleteChat(long tgChatId) throws ClientException {
        try {
            webClient
                .delete()
                .uri(uriBuilder -> uriBuilder.path(TG_ID_PATH)
                    .build(tgChatId)
                )
                .retrieve()
                .onStatus(HttpStatusCode::isError, response -> response.createException()
                    .flatMap(error -> {
                        throw new ErrorResponseException(
                            response.statusCode(), error.getResponseBodyAs(ErrorResponse.class)
                        );
                    })
                ).bodyToMono(String.class)
                .block();
        } catch (ErrorResponseException e) {
            logger.error(e);
            throw ScrapperClientException.wrapException(e);
        }
    }

    @Override
    public LinkResponse trackNewLink(long tgChatId, String url, String alias) throws ClientException {
        try {
            return webClient
                .post()
                .uri(LINKS_PATH)
                .header(TG_CHAT_ID_HEADER, String.valueOf(tgChatId))
                .body(Mono.just(new AddLinkRequest(URI.create(url), alias)), AddLinkRequest.class)
                .retrieve()
                .onStatus(HttpStatusCode::isError, response -> response.createException()
                    .flatMap(error -> {
                        throw new ErrorResponseException(
                            response.statusCode(), error.getResponseBodyAs(ErrorResponse.class)
                        );
                    })
                ).bodyToMono(LinkResponse.class)
                .block();
        } catch (ErrorResponseException e) {
            logger.error(e);
            throw ScrapperClientException.wrapException(e);
        }
    }

    @Override
    public LinkResponse trackNewLink(long tgChatId, String url) throws ClientException {
        try {
            return webClient
                .post()
                .uri(LINKS_PATH)
                .header(TG_CHAT_ID_HEADER, String.valueOf(tgChatId))
                .body(Mono.just(new AddLinkRequest(URI.create(url), null)), AddLinkRequest.class)
                .retrieve()
                .onStatus(HttpStatusCode::isError, response -> response.createException()
                    .flatMap(error -> {
                        throw new ErrorResponseException(
                            response.statusCode(), error.getResponseBodyAs(ErrorResponse.class)
                        );
                    })
                ).bodyToMono(LinkResponse.class)
                .block();
        } catch (ErrorResponseException e) {
            logger.error(e);
            throw ScrapperClientException.wrapException(e);
        }
    }

    @Override
    public void untrackLink(long tgChatId, String alias) throws ClientException {
        try {
            webClient
                .method(HttpMethod.DELETE)
                .uri(LINKS_PATH)
                .header(TG_CHAT_ID_HEADER, String.valueOf(tgChatId))
                .body(Mono.just(new AddLinkRequest(null, alias)), AddLinkRequest.class)
                .retrieve()
                .onStatus(HttpStatusCode::isError, response -> response.createException()
                    .flatMap(error -> {
                        throw new ErrorResponseException(
                            response.statusCode(), error.getResponseBodyAs(ErrorResponse.class)
                        );
                    })
                ).bodyToMono(LinkResponse.class)
                .block();
        } catch (ErrorResponseException e) {
            logger.error(e);
            throw ScrapperClientException.wrapException(e);
        }
    }

    @Override
    public ListLinksResponse getAllUserTracks(long tgChatId) throws ClientException {
        try {
            return webClient
                .get()
                .uri(LINKS_PATH)
                .header(TG_CHAT_ID_HEADER, String.valueOf(tgChatId))
                .retrieve()
                .onStatus(HttpStatusCode::isError, response -> response.createException()
                    .flatMap(error -> {
                        throw new ErrorResponseException(
                            response.statusCode(), error.getResponseBodyAs(ErrorResponse.class)
                        );
                    })
                ).bodyToMono(ListLinksResponse.class)
                .block();
        } catch (ErrorResponseException e) {
            logger.error(e);
            throw ScrapperClientException.wrapException(e);
        }
    }
}
