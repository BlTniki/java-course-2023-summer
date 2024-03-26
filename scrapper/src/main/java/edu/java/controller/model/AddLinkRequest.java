package edu.java.controller.model;

import jakarta.annotation.Nullable;
import jakarta.validation.constraints.NotNull;
import java.net.URI;

public record AddLinkRequest(
    @NotNull URI link,
    @Nullable String alias
) {}
