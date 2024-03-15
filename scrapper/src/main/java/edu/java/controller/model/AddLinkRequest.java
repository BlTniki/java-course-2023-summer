package edu.java.controller.model;

import jakarta.validation.constraints.NotNull;
import java.net.URI;

public record AddLinkRequest(
    @NotNull URI link,
    @NotNull String alias
) {}
