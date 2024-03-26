package edu.java.domain.link.service.github;

import edu.java.ScrapperApplicationTests;
import edu.java.client.exception.ClientException;
import edu.java.client.exception.ForbiddenClientException;
import edu.java.client.exception.ResourceNotFoundClientException;
import edu.java.client.github.GitHubClient;
import edu.java.client.github.model.Commit;
import edu.java.client.github.model.CommitResponse;
import edu.java.client.github.model.RepositoryActivityResponse;
import edu.java.client.github.model.RepositoryIssueResponse;
import edu.java.client.github.model.RepositoryResponse;
import edu.java.controller.model.ErrorCode;
import edu.java.domain.exception.CorruptedDataException;
import edu.java.domain.exception.EntityValidationFailedException;
import edu.java.domain.exception.ServiceException;
import java.time.OffsetDateTime;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

class GitHubLinkCheckerTest extends ScrapperApplicationTests {
    private final GitHubClient gitHubClient = mock(GitHubClient.class);
    private final GitHubLinkChecker gitHubLinkChecker = new GitHubLinkChecker(gitHubClient);

    @Test
    @DisplayName("Проверим, что мы возвращаем новые данные, если они есть")
    void checkRepo_shouldReturnNewData_whenLastUpdateIsAfterOldLastUpdate() throws ClientException {
        String owner = "testOwner";
        String repo = "testRepo";
        Map<String, String> trackedData = new HashMap<>();
        trackedData.put("resource", "REPO_ONLY");
        trackedData.put("owner", owner);
        trackedData.put("repo", repo);
        trackedData.put("last_update", "1970-01-01T00:00Z");

        RepositoryResponse repositoryResponse = new RepositoryResponse(0, null, OffsetDateTime.now(), OffsetDateTime.now());
        RepositoryIssueResponse repositoryIssueResponse = new RepositoryIssueResponse(0, null, OffsetDateTime.now(), null);
        RepositoryActivityResponse repositoryActivityResponse = new RepositoryActivityResponse(0, "sha", "main", "push", OffsetDateTime.now());
        CommitResponse commitResponse = new CommitResponse("sha", new Commit("foo"));

        when(gitHubClient.fetchRepository(owner, repo)).thenReturn(repositoryResponse);
        when(gitHubClient.fetchRepositoryIssues(owner, repo)).thenReturn(List.of(repositoryIssueResponse));
        when(gitHubClient.fetchRepositoryActivity(owner, repo)).thenReturn(List.of(repositoryActivityResponse));
        when(gitHubClient.fetchCommit(owner, repo, "sha")).thenReturn(commitResponse);

        Map<String, String> result = gitHubLinkChecker.check(trackedData);

        assertThat(result).containsKey("last_update");
        assertThat(OffsetDateTime.parse(result.get("last_update"))).isAfter(OffsetDateTime.parse(trackedData.get("last_update")));
    }

    @Test
    @DisplayName("Проверим, что мы валидируем данные")
    void checkRepo_shouldThrowCorruptedDataException_whenOwnerOrRepoIsNotSpecified() {
        Map<String, String> trackedData = new HashMap<>();
        trackedData.put("resource", "REPO_ONLY");

        assertThatThrownBy(() -> gitHubLinkChecker.check(trackedData))
            .isInstanceOf(CorruptedDataException.class)
            .hasMessageContaining("The owner is not specified")
            .hasFieldOrPropertyWithValue("errorCode", ErrorCode.INTERNAL_SERVER_ERROR);

        trackedData.put("owner", "testOwner");

        assertThatThrownBy(() -> gitHubLinkChecker.check(trackedData))
            .isInstanceOf(CorruptedDataException.class)
            .hasMessageContaining("The repo is not specified")
            .hasFieldOrPropertyWithValue("errorCode", ErrorCode.INTERNAL_SERVER_ERROR);
    }

    @Test
    @DisplayName("Проверим, что мы отлавливаем несуществование репозитория")
    void checkRepo_shouldThrowEntityValidationFailedException_whenRepoDoesNotExist() throws ClientException {
        String owner = "testOwner";
        String repo = "testRepo";
        Map<String, String> trackedData = new HashMap<>();
        trackedData.put("resource", "REPO_ONLY");
        trackedData.put("owner", owner);
        trackedData.put("repo", repo);

        when(gitHubClient.fetchRepository(owner, repo)).thenThrow(new ResourceNotFoundClientException("Repository not found", null));

        assertThatThrownBy(() -> gitHubLinkChecker.check(trackedData))
            .isInstanceOf(EntityValidationFailedException.class)
            .hasMessageContaining("Given repository is not exist or i have no permissions: " + owner + "/" + repo)
            .hasFieldOrPropertyWithValue("errorCode", ErrorCode.BAD_REQUEST);
    }

    @Test
    @DisplayName("Проверим, что мы отлавливаем неожиданные ошибки от клиента")
    void checkRepo_shouldThrowServiceException_whenUnexpectedErrorOccurs() throws ClientException {
        String owner = "testOwner";
        String repo = "testRepo";
        Map<String, String> trackedData = new HashMap<>();
        trackedData.put("resource", "REPO_ONLY");
        trackedData.put("owner", owner);
        trackedData.put("repo", repo);

        when(gitHubClient.fetchRepository(owner, repo)).thenThrow(new ClientException("Unexpected error", null));

        assertThatThrownBy(() -> gitHubLinkChecker.check(trackedData))
            .isInstanceOf(ServiceException.class)
            .hasMessageContaining("Unexpected error while fetching from GitHub")
            .hasFieldOrPropertyWithValue("errorCode", ErrorCode.INTERNAL_SERVER_ERROR);
    }

    @Test
    @DisplayName("Проверим, что мы отлавливаем неожиданные ошибки от клиента")
    void checkRepo_shouldThrowForbiddenClientException_whenAccessIsForbidden() throws ClientException {
        String owner = "testOwner";
        String repo = "testRepo";
        Map<String, String> trackedData = new HashMap<>();
        trackedData.put("resource", "REPO_ONLY");
        trackedData.put("owner", owner);
        trackedData.put("repo", repo);

        when(gitHubClient.fetchRepository(owner, repo)).thenThrow(new ForbiddenClientException("Access forbidden", null));

        assertThatThrownBy(() -> gitHubLinkChecker.check(trackedData))
            .isInstanceOf(EntityValidationFailedException.class)
            .hasMessageContaining("Given repository is not exist or i have no permissions: " + owner + "/" + repo)
            .hasFieldOrPropertyWithValue("errorCode", ErrorCode.BAD_REQUEST);
    }

    @Test
    @DisplayName("Проверим, что мы правильно представляем новые данные в виде сообщения")
    void toUpdateMessage_shouldReturnUpdateMessage() {
        Map<String, String> newData = new HashMap<>();
        newData.put("last_update", OffsetDateTime.now().toString());

        String updateMessage = gitHubLinkChecker.toUpdateMessage(newData);

        assertThat(updateMessage).isEqualTo("В репозитории произошло обновление");
    }
}
