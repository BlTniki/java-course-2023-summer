package edu.java.controller.model;

import edu.java.service.link.model.Link;
import jakarta.validation.constraints.NotNull;
import java.util.List;

public record ListLinksResponse(
    List<LinkResponse> links,
    int size
) {
    public static @NotNull ListLinksResponse from(@NotNull List<Link> links) {
        return new ListLinksResponse(
            links.stream().map(LinkResponse::from).toList(),
            links.size()
        );
    }
}
