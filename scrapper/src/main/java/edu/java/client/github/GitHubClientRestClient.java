package edu.java.client.github;

import edu.java.client.github.model.RepositoryResponse;
import org.springframework.web.client.RestClient;

public class GitHubClientRestClient implements GitHubClient {
    private final RestClient restClient;

    public GitHubClientRestClient(RestClient.Builder restClientBuilder) {
        this.restClient = restClientBuilder.baseUrl("https://echo.free.beeceptor.com").build();
    }

    @Override
    public RepositoryResponse fetchRepository() {
//        RepositoryRequest r = new RepositoryRequest(1);
//        var response =  restClient
//            .post()
//            .contentType(MediaType.APPLICATION_JSON)
//            .body(r)
//            .retrieve()
//            .toEntity(RepositoryResponse.class);


        return null;
    }
}
