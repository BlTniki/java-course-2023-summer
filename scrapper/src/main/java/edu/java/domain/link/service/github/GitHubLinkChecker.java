package edu.java.domain.link.service.github;

import edu.java.client.exception.ClientException;
import edu.java.client.exception.ForbiddenClientException;
import edu.java.client.exception.ResourceNotFoundClientException;
import edu.java.client.github.GitHubClient;
import edu.java.client.github.model.Commit;
import edu.java.client.github.model.RepositoryActivityResponse;
import edu.java.client.github.model.RepositoryIssueResponse;
import edu.java.client.github.model.RepositoryResponse;
import edu.java.controller.model.ErrorCode;
import edu.java.domain.exception.CorruptedDataException;
import edu.java.domain.exception.EntityValidationFailedException;
import edu.java.domain.exception.ServiceException;
import edu.java.domain.link.service.LinkChecker;
import jakarta.validation.constraints.NotNull;
import java.time.OffsetDateTime;
import java.util.Comparator;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;
import java.util.stream.Stream;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

public class GitHubLinkChecker implements LinkChecker {
    private static final Logger LOGGER = LogManager.getLogger();
    private static final String RESOURCE_KEY = "resource";
    private static final String OWNER_KEY = "owner";
    private static final String REPO_KEY = "repo";
    private static final String LAST_UPDATE_KEY = "last_update";
    private static final String LAST_COMMIT_TIMESTAMP_KEY = "last_commit_timestamp";
    private static final String NEW_COMMITS_MESSAGE_KEY = "ncm";
    private static final String EPOCH_START = "1970-01-01T00:00Z";
    private final GitHubClient gitHubClient;

    public GitHubLinkChecker(GitHubClient gitHubClient) {
        this.gitHubClient = gitHubClient;
    }

    private @NotNull Map<String, String> checkRepoActivities(
            String owner,
            String repo,
            Map<String, String> trackedData
    ) {
        Map<String, String> newData = new HashMap<>();
        OffsetDateTime oldLastCommitTimestamp = OffsetDateTime.parse(
            trackedData.getOrDefault(LAST_COMMIT_TIMESTAMP_KEY, EPOCH_START)
        );

        List<RepositoryActivityResponse> activities;
        try {
            activities = gitHubClient.fetchRepositoryActivity(owner, repo);
        } catch (ClientException e) {
            throw handleException(e);
        }

        var activitiesFiltered = activities.stream()
            .filter(activity -> activity.timestamp().isAfter(oldLastCommitTimestamp))
            .filter(activity -> !activity.activityType().equals("branch_deletion")) // такие активности имеют пустой sha
            .sorted(Comparator.comparing(RepositoryActivityResponse::timestamp).reversed())
            .toList();

        if (activitiesFiltered.isEmpty()) {
            return newData;
        }

        // set new last_commit_timestamp
        newData.put(LAST_COMMIT_TIMESTAMP_KEY, activitiesFiltered.getFirst().timestamp().toString());

        String newCommitsMessages = activitiesFiltered.stream()
            .map(activity -> {
                Commit commit;
                try {
                    commit = gitHubClient.fetchCommit(owner, repo, activity.after()).commit();
                } catch (ClientException e) {
                    throw handleException(e);
                }
                return activity.ref() + ": " + commit.message();
            }).collect(Collectors.joining("\n"));

        newData.put(NEW_COMMITS_MESSAGE_KEY, newCommitsMessages);

        return newData;
    }

    private static ServiceException handleException(ClientException e) {
        LOGGER.error("Unexpected error");
        LOGGER.error(e);
        return new ServiceException("Unexpected error while fetching from GitHub", ErrorCode.INTERNAL_SERVER_ERROR);
    }

    private @NotNull Map<String, String> checkRepo(Map<String, String> trackedData) throws CorruptedDataException {
        String owner = trackedData.getOrDefault(OWNER_KEY, null);
        String repo = trackedData.getOrDefault(REPO_KEY, null);

        if (owner == null) {
            throw new CorruptedDataException("The owner is not specified", ErrorCode.INTERNAL_SERVER_ERROR);
        }
        if (repo == null) {
            throw new CorruptedDataException("The repo is not specified", ErrorCode.INTERNAL_SERVER_ERROR);
        }

        RepositoryResponse repositoryResponse;
        List<RepositoryIssueResponse> repositoryIssueResponses;
        try {
            repositoryResponse = gitHubClient.fetchRepository(owner, repo);
            repositoryIssueResponses = gitHubClient.fetchRepositoryIssues(owner, repo);
        } catch (ForbiddenClientException | ResourceNotFoundClientException e) {
            throw new EntityValidationFailedException(
                "Given repository is not exist or i have no permissions: " + owner + "/" + repo,
                ErrorCode.URL_VALIDATION_FAILED
            );
        } catch (ClientException e) {
            throw handleException(e);
        }

        Stream<OffsetDateTime> lastUpdateStream;
        if (repositoryIssueResponses.isEmpty()) {
            lastUpdateStream = Stream.of(
                repositoryResponse.updatedAt(),
                repositoryResponse.pushedAt()
            );
        } else {
            lastUpdateStream = Stream.of(
                repositoryResponse.updatedAt(),
                repositoryResponse.pushedAt(),
                repositoryIssueResponses.getFirst().updatedAt()
            );
        }

        OffsetDateTime newestLastUpdateInRepo = lastUpdateStream
            .max(Comparator.naturalOrder())
            .orElseThrow(() -> new IllegalStateException("No dates available"));

        OffsetDateTime oldLastUpdate = OffsetDateTime.parse(trackedData.getOrDefault(
            LAST_UPDATE_KEY,
            EPOCH_START
        ));

        if (newestLastUpdateInRepo.isAfter(oldLastUpdate)) {
            Map<String, String> newData = new HashMap<>(checkRepoActivities(owner, repo, trackedData));
            newData.put(LAST_UPDATE_KEY, newestLastUpdateInRepo.toString());
            return newData;
        }
        return new HashMap<>();
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
        String newCommits = newData.getOrDefault(NEW_COMMITS_MESSAGE_KEY, null);
        String lastUpdate = newData.getOrDefault(LAST_UPDATE_KEY, null);

        StringBuilder builder = new StringBuilder();

        if (newCommits != null) {
            builder.append("Новые коммиты:");
            builder.append(newCommits);

            if (lastUpdate != null) {
                builder.append("\nА также другие обновления");
            }
        } else if (lastUpdate != null) {
            builder.append("В репозитории произошло обновление");
        }

        return builder.toString();
    }
}
