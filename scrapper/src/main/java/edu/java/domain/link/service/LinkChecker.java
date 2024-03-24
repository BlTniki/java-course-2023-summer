package edu.java.domain.link.service;

import edu.java.domain.exception.CorruptedDataException;
import jakarta.validation.constraints.NotNull;
import java.util.Map;

public interface LinkChecker {
    /**
     * Проверяет сервис на наличие обновлений основываясь на отслеживаемых данных.
     * Важно отметить, метод возвращает только новые данные от сервиса
     * и их требуется влить в отслеживаемые данные.
     * @param trackedData отслеживаемые данные сервиса
     * @return новые данные от сервиса.
     */
    @NotNull Map<String, String> check(@NotNull Map<String, String> trackedData) throws CorruptedDataException;

    @NotNull String toUpdateMessage(@NotNull Map<String, String> newData);
}
