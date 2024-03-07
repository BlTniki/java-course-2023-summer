package edu.java.domain.dto;

import java.time.OffsetDateTime;

public record LinkDto(
    Long id,
    String url,
    OffsetDateTime lastUpdate
) {}
