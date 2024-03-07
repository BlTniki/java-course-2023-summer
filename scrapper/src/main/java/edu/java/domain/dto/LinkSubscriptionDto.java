package edu.java.domain.dto;

public record LinkSubscriptionDto(
    Long id,
    Long chatId,
    Long linkId,
    String alias
) {}
