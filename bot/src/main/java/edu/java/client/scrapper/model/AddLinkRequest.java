package edu.java.client.scrapper.model;

import java.net.URI;

public record AddLinkRequest(
    URI link,
    String alias
) {}
