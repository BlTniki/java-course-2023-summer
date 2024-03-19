package edu.java.domain.dto;

public record SubscriptionDto(
    Long id,
    Long chatId,
    Long linkId,
    String alias
) {}
