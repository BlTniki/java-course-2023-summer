package edu.java.domain.link.dto;

public record SubscriptionDto(
    Long id,
    Long chatId,
    Long linkId,
    String alias
) {}
