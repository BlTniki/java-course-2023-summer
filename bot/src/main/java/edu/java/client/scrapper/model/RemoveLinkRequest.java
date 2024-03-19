package edu.java.client.scrapper.model;

import java.net.URI;

public record RemoveLinkRequest(
    URI link,
    String alias
) {}
