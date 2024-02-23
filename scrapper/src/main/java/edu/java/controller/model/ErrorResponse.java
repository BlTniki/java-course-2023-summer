package edu.java.controller.model;

public record ErrorResponse(
    String description,
    String code,
    String exceptionName,
    String exceptionMessage,
    String[] stacktrace
) {}
