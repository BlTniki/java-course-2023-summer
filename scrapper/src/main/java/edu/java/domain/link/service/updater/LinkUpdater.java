package edu.java.domain.link.service.updater;

import jakarta.validation.constraints.NotNull;
import java.time.OffsetDateTime;

/**
 * Классы с данным интерфейсом предполагают
 * функционал обнаружения обновлений ресурсов и рассылки обновлений подписчикам.
 */
public interface LinkUpdater {
    /**
     * Метод, что проверяет ресурсы на наличие обновлений
     * и рассылает обновления подписчикам.
     * @param from с какого периода проверять обновления
     */
    void checkUpdatesAndNotify(@NotNull OffsetDateTime from);
}
