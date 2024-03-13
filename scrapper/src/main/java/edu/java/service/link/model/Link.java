package edu.java.service.link.model;

import java.net.URI;

public record Link(
    long id,
    URI link,
    String alias
) {}
