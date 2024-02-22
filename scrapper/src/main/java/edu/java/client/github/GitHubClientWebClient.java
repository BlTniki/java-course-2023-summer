package edu.java.client.github;

import edu.java.client.exception.ClientException;
import edu.java.client.github.model.RepositoryActivityResponse;
import edu.java.client.github.model.RepositoryIssueResponse;
import edu.java.client.github.model.RepositoryResponse;
import jakarta.validation.constraints.NotNull;
import java.util.List;
import org.apache.logging.log4j.Logger;
import org.springframework.http.HttpStatusCode;
import org.springframework.web.client.HttpClientErrorException;
import org.springframework.web.reactive.function.client.WebClient;

public class GitHubClientWebClient implements GitHubClient {
    public static final String ERROR_HEADER = "Got an error from API: ";
    private final Logger logger;
    private final WebClient webClient;

    public GitHubClientWebClient(WebClient.Builder webClientBuilder, Logger logger) {
        this.webClient = webClientBuilder.build();
        this.logger = logger;
    }

    @Override
    public RepositoryResponse fetchRepository(@NotNull String owner, @NotNull String repo) throws ClientException {
        try {
            return webClient
                .get()
                .uri(uriBuilder -> uriBuilder.path("/repos/{owner}/{repo}")
                                            .build(owner, repo)
                )
                .retrieve()
                .onStatus(HttpStatusCode::isError, response -> response.bodyToMono(String.class)
                    .flatMap(errorBody -> {
                        throw new HttpClientErrorException(response.statusCode(), errorBody);
                    }))
                .bodyToMono(RepositoryResponse.class)
                .block();
        } catch (HttpClientErrorException e) {
            logger.error(ERROR_HEADER + e);
            throw ClientException.wrapException(e);
        }
    }

    @Override
    public List<RepositoryIssueResponse> fetchRepositoryIssues(@NotNull String owner, @NotNull String repo)
            throws ClientException {
        try {
            return webClient
                .get()
                .uri(uriBuilder -> uriBuilder.path("/repos/{owner}/{repo}/issues")
                    .build(owner, repo)
                )
                .retrieve()
                .onStatus(HttpStatusCode::isError, response -> response.bodyToMono(String.class)
                    .flatMap(errorBody -> {
                        throw new HttpClientErrorException(response.statusCode(), errorBody);
                    }))
                .bodyToFlux(RepositoryIssueResponse.class)
                .collectList()
                .block();
        } catch (HttpClientErrorException e) {
            logger.error(ERROR_HEADER + e);
            throw ClientException.wrapException(e);
        }
    }

    @Override
    public List<RepositoryActivityResponse> fetchRepositoryActivity(String owner, String repo)
            throws ClientException {
        try {
            return webClient
                .get()
                .uri(uriBuilder -> uriBuilder.path("/repos/{owner}/{repo}/activity")
                    .build(owner, repo)
                )
                .retrieve()
                .onStatus(HttpStatusCode::isError, response -> response.bodyToMono(String.class)
                    .flatMap(errorBody -> {
                        throw new HttpClientErrorException(response.statusCode(), errorBody);
                    }))
                .bodyToFlux(RepositoryActivityResponse.class)
                .collectList()
                .block();
        } catch (HttpClientErrorException e) {
            logger.error(ERROR_HEADER + e);
            throw ClientException.wrapException(e);
        }
    }
}
