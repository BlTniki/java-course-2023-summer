package edu.java.domain.dto;

import java.net.URI;
import java.time.OffsetDateTime;

public record LinkDto(
    Long id,
    URI url,
    OffsetDateTime lastUpdate
) {}
