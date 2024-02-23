package edu.java.controller.model;

import java.net.URI;

public record AddLinkRequest(
    URI link,
    String alias
) {}
