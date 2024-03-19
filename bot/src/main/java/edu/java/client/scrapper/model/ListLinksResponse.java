package edu.java.client.scrapper.model;

import java.util.List;

public record ListLinksResponse(
    List<LinkResponse> links,
    int size
) {}
