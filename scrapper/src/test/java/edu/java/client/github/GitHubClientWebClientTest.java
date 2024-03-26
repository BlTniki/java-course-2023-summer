package edu.java.client.github;

import com.github.tomakehurst.wiremock.stubbing.Scenario;
import edu.java.ScrapperApplicationTests;
import edu.java.client.exception.ClientException;
import edu.java.client.exception.ResourceNotFoundClientException;
import edu.java.client.github.model.Commit;
import edu.java.client.github.model.CommitResponse;
import edu.java.client.github.model.PullRequestResponse;
import edu.java.client.github.model.RepositoryActivityResponse;
import edu.java.client.github.model.RepositoryIssueResponse;
import edu.java.client.github.model.RepositoryResponse;
import java.time.OffsetDateTime;
import java.util.List;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.reactive.function.client.WebClient;
import static com.github.tomakehurst.wiremock.client.WireMock.aResponse;
import static com.github.tomakehurst.wiremock.client.WireMock.get;
import static com.github.tomakehurst.wiremock.client.WireMock.urlPathEqualTo;
import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

class GitHubClientWebClientTest extends ScrapperApplicationTests {
    @Autowired
    WebClient.Builder webClientBuilder;
    @Autowired
    GitHubClient gitHubClient;

    @Test
    @DisplayName("Проверим запрос на получение существующего репозитория")
    void fetchRepository_success() throws ClientException {
        // Arrange
        String owner = "testOwner";
        String repo = "testRepo";
        wireMockServer.stubFor(
            get(urlPathEqualTo("/repos/" + owner + "/" + repo))
            .willReturn(
                aResponse()
                .withStatus(200)
                .withHeader("Content-Type", "application/json")
                .withBodyFile("client/github/get_repo_success.json")
            )
        );
        RepositoryResponse expected = new RepositoryResponse(
            752724284,
            "BlTniki/java-course-2023-summer",
            OffsetDateTime.parse("2024-02-04T16:29:00Z"),
            OffsetDateTime.parse("2024-02-22T10:22:55Z")
        );

        // Act
        RepositoryResponse actual = gitHubClient.fetchRepository(owner, repo);

        // Assert
        assertThat(actual).isEqualTo(expected);
    }

    @Test
    @DisplayName("Проверим что метод получения репозитория таки кидает исключение")
    void fetchRepository_clientException() {
        // Arrange
        String owner = "testOwner";
        String repo = "testRepo";
        wireMockServer.stubFor(
            get(urlPathEqualTo("/repos/" + owner + "/" + repo))
                .willReturn(
                    aResponse()
                        .withStatus(418)
                )
        );
        // Assert
        assertThatThrownBy(() -> gitHubClient.fetchRepository(owner, repo))
            .isInstanceOf(ClientException.class);
    }

    @Test
    @DisplayName("Проверим что отрабатывает механизм retry на получение существующего репозитория")
    void fetchRepository_reply() {
        // Arrange
        String owner = "testOwner";
        String repo = "testRepo";
        String scenarioName = "Repo Scenario";

        wireMockServer.stubFor(
            get(urlPathEqualTo("/repos/" + owner + "/" + repo))
                .inScenario(scenarioName)
                .whenScenarioStateIs(Scenario.STARTED)
                .willReturn(aResponse().withStatus(420))
                .willSetStateTo("Second Request")
        );

        wireMockServer.stubFor(
            get(urlPathEqualTo("/repos/" + owner + "/" + repo))
                .inScenario(scenarioName)
                .whenScenarioStateIs("Second Request")
                .willReturn(aResponse().withStatus(404))
        );

        // Assert
        assertThatThrownBy(() -> gitHubClient.fetchRepository(owner, repo))
            .isInstanceOf(ResourceNotFoundClientException.class);
    }

    @Test
    @DisplayName("Проверим запрос на получение активностей репозитория")
    void fetchRepositoryActivity_success() throws ClientException {
        // Arrange
        String owner = "testOwner";
        String repo = "testRepo";
        wireMockServer.stubFor(
            get(urlPathEqualTo("/repos/" + owner + "/" + repo + "/activity"))
                .willReturn(
                    aResponse()
                        .withStatus(200)
                        .withHeader("Content-Type", "application/json")
                        .withBodyFile("client/github/get_repo_activities_success.json")
                )
        );
        List<RepositoryActivityResponse> expected = List.of(
            new RepositoryActivityResponse(
                17220033061L,
                "3e78dc89827691e9a2bfcc9d6d4ce5d93403d3ad",
                "refs/heads/hw2",
                "push",
                OffsetDateTime.parse("2024-02-22T19:52:19Z")
            ),
            new RepositoryActivityResponse(
                17210754997L,
                "9b9dfe07a621c4d8a3557b9b2c604c6cc27c3a49",
                "refs/heads/hw2",
                "push",
                OffsetDateTime.parse("2024-02-22T10:22:55Z")
            )
        );

        // Act
        List<RepositoryActivityResponse> actual = gitHubClient.fetchRepositoryActivity(owner, repo);

        // Assert
        assertThat(actual)
            .containsExactlyInAnyOrderElementsOf(expected);
    }

    @Test
    @DisplayName("Проверим что мы правильно обрабатываем пустые активности репозитория")
    void fetchRepositoryActivity_empty() throws ClientException {
        // Arrange
        String owner = "testOwner";
        String repo = "testRepo";
        wireMockServer.stubFor(
            get(urlPathEqualTo("/repos/" + owner + "/" + repo + "/activity"))
                .willReturn(
                    aResponse()
                        .withStatus(200)
                        .withHeader("Content-Type", "application/json")
                        .withBodyFile("client/github/get_repo_activities_empty.json")
                )
        );
        List<RepositoryActivityResponse> expected = List.of();

        // Act
        List<RepositoryActivityResponse> actual = gitHubClient.fetchRepositoryActivity(owner, repo);

        // Assert
        assertThat(actual)
            .containsExactlyInAnyOrderElementsOf(expected);
    }

    @Test
    @DisplayName("Проверим что метод получения активностей таки кидает исключение")
    void fetchRepositoryActivity_clientException() {
        // Arrange
        String owner = "testOwner";
        String repo = "testRepo";
        wireMockServer.stubFor(
            get(urlPathEqualTo("/repos/" + owner + "/" + repo + "/activity"))
                .willReturn(
                    aResponse()
                        .withStatus(418)
                )
        );
        // Assert
        assertThatThrownBy(() -> gitHubClient.fetchRepositoryActivity(owner, repo))
            .isInstanceOf(ClientException.class);
    }

    @Test
    @DisplayName("Проверим что отрабатывает механизм retry на получение активностей репозитория")
    void fetchRepositoryActivity_reply() {
        // Arrange
        String owner = "testOwner";
        String repo = "testRepo";
        String scenarioName = "Repo Scenario";

        wireMockServer.stubFor(
            get(urlPathEqualTo("/repos/" + owner + "/" + repo + "/activity"))
                .inScenario(scenarioName)
                .whenScenarioStateIs(Scenario.STARTED)
                .willReturn(aResponse().withStatus(420))
                .willSetStateTo("Second Request")
        );

        wireMockServer.stubFor(
            get(urlPathEqualTo("/repos/" + owner + "/" + repo + "/activity"))
                .inScenario(scenarioName)
                .whenScenarioStateIs("Second Request")
                .willReturn(aResponse().withStatus(404))
        );

        // Assert
        assertThatThrownBy(() -> gitHubClient.fetchRepositoryActivity(owner, repo))
            .isInstanceOf(ResourceNotFoundClientException.class);
    }

    @Test
    @DisplayName("Проверим запрос на получение вопросов репозитория")
    void fetchRepositoryIssues_success() throws ClientException {
        // Arrange
        String owner = "testOwner";
        String repo = "testRepo";
        wireMockServer.stubFor(
            get(urlPathEqualTo("/repos/" + owner + "/" + repo + "/issues"))
                .willReturn(
                    aResponse()
                        .withStatus(200)
                        .withHeader("Content-Type", "application/json")
                        .withBodyFile("client/github/get_repo_issues_success.json")
                )
        );
        List<RepositoryIssueResponse> expected = List.of(
            new RepositoryIssueResponse(
                2132900274L,
                "Hw1 Done",
                OffsetDateTime.parse("2024-02-21T07:04:49Z"),
                new PullRequestResponse("https://api.github.com/repos/BlTniki/java-course-2023-summer/pulls/1", null)

            ),
            new RepositoryIssueResponse(
                2149903134L,
                "wololo",
                OffsetDateTime.parse("2024-02-22T20:43:16Z"),
                null
            )
        );

        // Act
        List<RepositoryIssueResponse> actual = gitHubClient.fetchRepositoryIssues(owner, repo);

        // Assert
        assertThat(actual)
            .containsExactlyInAnyOrderElementsOf(expected);
    }

    @Test
    @DisplayName("Проверим что мы правильно обрабатываем пустые вопросов репозитория")
    void fetchRepositoryIssues_empty() throws ClientException {
        // Arrange
        String owner = "testOwner";
        String repo = "testRepo";
        wireMockServer.stubFor(
            get(urlPathEqualTo("/repos/" + owner + "/" + repo + "/issues"))
                .willReturn(
                    aResponse()
                        .withStatus(200)
                        .withHeader("Content-Type", "application/json")
                        .withBodyFile("client/github/get_repo_issues_empty.json")
                )
        );
        List<RepositoryIssueResponse> expected = List.of();

        // Act
        List<RepositoryIssueResponse> actual = gitHubClient.fetchRepositoryIssues(owner, repo);

        // Assert
        assertThat(actual)
            .containsExactlyInAnyOrderElementsOf(expected);
    }

    @Test
    @DisplayName("Проверим что метод получения вопросов таки кидает исключение")
    void fetchRepositoryIssues_clientException() {
        // Arrange
        String owner = "testOwner";
        String repo = "testRepo";
        wireMockServer.stubFor(
            get(urlPathEqualTo("/repos/" + owner + "/" + repo + "/issues"))
                .willReturn(
                    aResponse()
                        .withStatus(418)
                )
        );
        // Assert
        assertThatThrownBy(() -> gitHubClient.fetchRepositoryIssues(owner, repo))
            .isInstanceOf(ClientException.class);
    }

    @Test
    @DisplayName("Проверим что отрабатывает механизм retry на получение вопросов репозитория")
    void fetchRepositoryIssues_reply() {
        // Arrange
        String owner = "testOwner";
        String repo = "testRepo";
        String scenarioName = "Repo Scenario";

        wireMockServer.stubFor(
            get(urlPathEqualTo("/repos/" + owner + "/" + repo + "/issues"))
                .inScenario(scenarioName)
                .whenScenarioStateIs(Scenario.STARTED)
                .willReturn(aResponse().withStatus(420))
                .willSetStateTo("Second Request")
        );

        wireMockServer.stubFor(
            get(urlPathEqualTo("/repos/" + owner + "/" + repo + "/issues"))
                .inScenario(scenarioName)
                .whenScenarioStateIs("Second Request")
                .willReturn(aResponse().withStatus(404))
        );

        // Assert
        assertThatThrownBy(() -> gitHubClient.fetchRepositoryIssues(owner, repo))
            .isInstanceOf(ResourceNotFoundClientException.class);
    }

    @Test
    @DisplayName("Проверим запрос на получение коммита")
    void fetchCommit_success() throws ClientException {
        // Arrange
        String owner = "testOwner";
        String repo = "testRepo";
        String sha = "testSha";
        wireMockServer.stubFor(
            get(urlPathEqualTo("/repos/" + owner + "/" + repo + "/commits/" + sha))
                .willReturn(
                    aResponse()
                        .withStatus(200)
                        .withHeader("Content-Type", "application/json")
                        .withBodyFile("client/github/get_commit_success.json")
                )
        );
        CommitResponse expected = new CommitResponse(
            "5a671c1acdfa36e53c592fb8f1a0863053b1ce3a",
            new Commit("delete trash file")
        );

        // Act
        CommitResponse actual = gitHubClient.fetchCommit(owner, repo, sha);

        // Assert
        assertThat(actual).isEqualTo(expected);
    }

    @Test
    @DisplayName("Проверим что метод получения коммита таки кидает исключение")
    void fetchCommit_clientException() {
        // Arrange
        String owner = "testOwner";
        String repo = "testRepo";
        String sha = "testSha";
        wireMockServer.stubFor(
            get(urlPathEqualTo("/repos/" + owner + "/" + repo + "/commits/" + sha))
                .willReturn(aResponse().withStatus(404))
        );

        // Assert
        assertThatThrownBy(() -> gitHubClient.fetchCommit(owner, repo, sha))
            .isInstanceOf(ResourceNotFoundClientException.class);
    }

    @Test
    @DisplayName("Проверим что отрабатывает механизм retry на получение коммита")
    void fetchCommit_reply() {
        // Arrange
        String owner = "testOwner";
        String repo = "testRepo";
        String scenarioName = "Repo Scenario";

        wireMockServer.stubFor(
            get(urlPathEqualTo("/repos/" + owner + "/" + repo + "/issues"))
                .inScenario(scenarioName)
                .whenScenarioStateIs(Scenario.STARTED)
                .willReturn(aResponse().withStatus(420))
                .willSetStateTo("Second Request")
        );

        wireMockServer.stubFor(
            get(urlPathEqualTo("/repos/" + owner + "/" + repo + "/issues"))
                .inScenario(scenarioName)
                .whenScenarioStateIs("Second Request")
                .willReturn(aResponse().withStatus(404))
        );

        // Assert
        assertThatThrownBy(() -> gitHubClient.fetchRepositoryIssues(owner, repo))
            .isInstanceOf(ResourceNotFoundClientException.class);
    }
}
