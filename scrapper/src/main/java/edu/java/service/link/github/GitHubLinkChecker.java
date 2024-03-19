package edu.java.service.link.github;

import edu.java.client.exception.ClientException;
import edu.java.client.exception.ForbiddenClientException;
import edu.java.client.exception.ResourceNotFoundClientException;
import edu.java.client.github.GitHubClient;
import edu.java.client.github.model.RepositoryActivityResponse;
import edu.java.client.github.model.RepositoryIssueResponse;
import edu.java.client.github.model.RepositoryResponse;
import edu.java.controller.model.ErrorCode;
import edu.java.service.exception.CorruptedDataException;
import edu.java.service.exception.EntityValidationFailedException;
import edu.java.service.exception.ServiceException;
import edu.java.service.link.LinkChecker;
import jakarta.validation.constraints.NotNull;
import java.time.OffsetDateTime;
import java.util.Comparator;
import java.util.HashMap;
import java.util.Map;
import java.util.stream.Stream;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

public class GitHubLinkChecker implements LinkChecker {
    private static final Logger LOGGER = LogManager.getLogger();
    private static final String RESOURCE_KEY = "resource";
    private static final String OWNER_KEY = "owner";
    private static final String REPO_KEY = "repo";
    private static final String LAST_UPDATE_KEY = "last_update";
    private final GitHubClient gitHubClient;

    public GitHubLinkChecker(GitHubClient gitHubClient) {
        this.gitHubClient = gitHubClient;
    }

    private @NotNull Map<String, String> checkRepo(Map<String, String> trackedData) throws CorruptedDataException {
        String owner = trackedData.getOrDefault(OWNER_KEY, null);
        String repo = trackedData.getOrDefault(REPO_KEY, null);
        Map<String, String> newData = new HashMap<>();

        if (owner == null) {
            throw new CorruptedDataException("The owner is not specified", ErrorCode.INTERNAL_SERVER_ERROR);
        }
        if (repo == null) {
            throw new CorruptedDataException("The repo is not specified", ErrorCode.INTERNAL_SERVER_ERROR);
        }

        RepositoryResponse repositoryResponse;
        RepositoryIssueResponse repositoryIssueResponse;
        RepositoryActivityResponse repositoryActivityResponse;
        try {
            repositoryResponse = gitHubClient.fetchRepository(owner, repo);
            repositoryIssueResponse = gitHubClient.fetchRepositoryIssues(owner, repo).getFirst();
            repositoryActivityResponse = gitHubClient.fetchRepositoryActivity(owner, repo).getFirst();
        } catch (ForbiddenClientException | ResourceNotFoundClientException e) {
            throw new EntityValidationFailedException(
                "Given repository is not exist or i have no permissions: " + owner + "/" + repo,
                ErrorCode.BAD_REQUEST
            );
        } catch (ClientException e) {
            LOGGER.error("Unexpected error");
            LOGGER.error(e);
            throw new ServiceException("Unexpected error while fetching from GitHub", ErrorCode.INTERNAL_SERVER_ERROR);
        }

        OffsetDateTime newestLastUpdateInRepo = Stream.of(
            repositoryResponse.updatedAt(),
            repositoryResponse.pushedAt(),
            repositoryIssueResponse.updatedAt(),
            repositoryActivityResponse.timestamp()
        ).max(Comparator.naturalOrder())
            .orElseThrow(() -> new IllegalStateException("No dates available"));

        OffsetDateTime oldLastUpdate = OffsetDateTime.parse(trackedData.getOrDefault(
            LAST_UPDATE_KEY,
            "1970-01-01T00:00Z"
        ));

        if (newestLastUpdateInRepo.isAfter(oldLastUpdate)) {
            newData.put(LAST_UPDATE_KEY, newestLastUpdateInRepo.toString());
        }

        return newData;
    }


    @Override
    public Map<String, String> check(Map<String, String> trackedData) throws CorruptedDataException {
        ResourceType resourceType;
        try {
            resourceType = ResourceType.valueOf(trackedData.getOrDefault(RESOURCE_KEY, null));
        } catch (IllegalArgumentException e) {
            throw new CorruptedDataException(
                "Bad resource type: " + trackedData.getOrDefault(RESOURCE_KEY, null),
                ErrorCode.INTERNAL_SERVER_ERROR
            );
        }

        if (resourceType.equals(ResourceType.REPO_ONLY)) {
            return checkRepo(trackedData);
        }

        throw new UnsupportedOperationException("Unsupported resource: " + resourceType);
    }

    @Override
    public String toUpdateMessage(Map<String, String> newData) {
        return "В репозитории произошло обновление";
    }
}
