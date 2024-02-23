package edu.java.controller.model;

import java.net.URI;

public record LinkResponse(
    long id,
    URI link,
    String alias
) {}
