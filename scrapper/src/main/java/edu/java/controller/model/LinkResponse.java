package edu.java.controller.model;

import edu.java.domain.link.dto.Link;
import jakarta.validation.constraints.NotNull;
import java.net.URI;

public record LinkResponse(
    long id,
    URI link,
    String alias
) {
    public static @NotNull LinkResponse from(@NotNull Link link) {
        return new LinkResponse(link.id(), link.link(), link.alias());
    }
}
