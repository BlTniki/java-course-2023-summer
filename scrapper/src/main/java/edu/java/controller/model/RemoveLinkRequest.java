package edu.java.controller.model;

import jakarta.validation.constraints.NotNull;

public record RemoveLinkRequest(
    @NotNull String alias
) {}
