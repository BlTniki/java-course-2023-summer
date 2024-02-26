package edu.java.client.scrapper.model;

import java.util.List;

public record ErrorResponse(
    String description,
    String code,
    String exceptionName,
    String exceptionMessage,
    List<String> stacktrace
) {}
