package edu.java.client.github;

import edu.java.client.exception.ClientException;
import edu.java.client.github.model.RepositoryActivityResponse;
import edu.java.client.github.model.RepositoryIssueResponse;
import edu.java.client.github.model.RepositoryResponse;
import jakarta.validation.constraints.NotNull;
import java.util.List;

public interface GitHubClient {
    /**
     * Возвращает репозиторий.
     * @param owner владелец репозитория
     * @param repo название репозитория
     * @return репозиторий
     * @throws ClientException если код ответа не 2xx
     */
    @NotNull RepositoryResponse fetchRepository(@NotNull String owner, @NotNull String repo) throws ClientException;

    /**
     * Возвращает список issues репозитория.
     * @param owner владелец репозитория
     * @param repo название репозитория
     * @return список issues репозитория
     * @throws ClientException если код ответа не 2xx
     */
    @NotNull List<RepositoryIssueResponse> fetchRepositoryIssues(@NotNull String owner, @NotNull String repo)
        throws ClientException;

    /**
     * Возвращает список активностей репозитория.
     * @param owner владелец репозитория
     * @param repo название репозитория
     * @return список активностей репозитория
     * @throws ClientException если код ответа не 2xx
     */
    @NotNull List<RepositoryActivityResponse> fetchRepositoryActivity(@NotNull String owner, @NotNull String repo)
        throws ClientException;
}
