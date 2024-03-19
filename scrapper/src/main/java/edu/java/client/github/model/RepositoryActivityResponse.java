package edu.java.client.github.model;

import java.time.OffsetDateTime;

public record RepositoryActivityResponse(
    long id,
    OffsetDateTime timestamp
) {
}
