package edu.java.controller.model;

import java.net.URI;

public record RemoveLinkRequest(
    URI link,
    String alias
) {}
