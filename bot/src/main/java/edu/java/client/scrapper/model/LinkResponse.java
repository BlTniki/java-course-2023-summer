package edu.java.client.scrapper.model;

import java.net.URI;

public record LinkResponse(
    long id,
    URI link,
    String alias
) {}
