package edu.java.service.link.model;

import java.util.Map;

/**
 * Этот класс хранит описание и актуальные данные отслеживаемой ссылки.
 * @param serviceType тип сервиса (Github, StackOverflow и т.д.)
 * @param trackedData разнообразные данные, необходимые для конкретного сервиса
 */
public record LinkDescriptor(
    ServiceType serviceType,
    Map<String, String> trackedData
) {}
