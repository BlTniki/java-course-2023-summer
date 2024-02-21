package edu.java.client.github.model;

import java.time.OffsetDateTime;

public record RepositoryIssueResponse(
    long id,
    String title,
    OffsetDateTime updatedAt,
    String pullRequest
) {
}
