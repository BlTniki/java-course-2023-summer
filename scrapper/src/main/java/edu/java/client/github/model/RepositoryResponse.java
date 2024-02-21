package edu.java.client.github.model;

import java.time.OffsetDateTime;

public record RepositoryResponse(
    long id,
    String name,
    OffsetDateTime updatedAt,
    OffsetDateTime pushedAt
) {}
