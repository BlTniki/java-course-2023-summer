package edu.java.client.github;

import edu.java.client.github.model.RepositoryResponse;

public interface GitHubClient {
    RepositoryResponse fetchRepository();
}
