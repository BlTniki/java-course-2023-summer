package edu.java.controller.model;

import io.swagger.v3.oas.annotations.media.Schema;

@Schema(name = "ErrorCode", description = "Перечисление кодов ошибок")
public enum ErrorCode {
    @Schema(description = "Internal server error")
    INTERNAL_SERVER_ERROR,
    @Schema(description = "Bad request")
    BAD_REQUEST,
    @Schema(description = "Telegram chat not found")
    TG_CHAT_NOT_FOUND,
    @Schema(description = "Telegram chat already exists")
    TG_CHAT_ALREADY_EXIST,
    @Schema(description = "Telegram chat validation failed")
    TG_CHAT_VALIDATION_FAILED,
    @Schema(description = "URL not found")
    URL_NOT_FOUND,
    @Schema(description = "URL already exists")
    URL_ALREADY_EXIST,
    @Schema(description = "URL validation failed")
    URL_VALIDATION_FAILED,
    @Schema(description = "URL not supported")
    URL_NOT_SUPPORTED,
    @Schema(description = "Alias not found")
    ALIAS_NOT_FOUND,
    @Schema(description = "Alias already exists")
    ALIAS_ALREADY_EXIST,
    @Schema(description = "Alias validation failed")
    ALIAS_VALIDATION_FAILED
}
