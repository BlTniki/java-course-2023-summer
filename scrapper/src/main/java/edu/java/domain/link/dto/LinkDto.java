package edu.java.domain.link.dto;

import java.net.URI;
import java.time.OffsetDateTime;

public record LinkDto(
    Long id,
    URI url,
    String serviceType,
    String trackedData,
    OffsetDateTime lastCheck
) {}
