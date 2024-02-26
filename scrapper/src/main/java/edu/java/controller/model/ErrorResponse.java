package edu.java.controller.model;

import io.swagger.v3.oas.annotations.media.Schema;
import java.util.List;

public record ErrorResponse(
    String description,
    @Schema(implementation = ErrorCode.class, defaultValue = "SOME_ERROR_CODE")
    ErrorCode code,
    String exceptionName,
    String exceptionMessage,
    List<String> stacktrace
) {}
