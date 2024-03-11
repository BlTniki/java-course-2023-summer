package edu.java.domain.dao.subscription;

import edu.java.domain.dto.SubscriptionDto;
import jakarta.validation.constraints.NotNull;
import java.util.List;

public interface SubscriptionDao {
    @NotNull List<SubscriptionDto> findAll();

    @NotNull SubscriptionDto findById(long id);

    @NotNull List<SubscriptionDto> findByChatId(long chatId);

    @NotNull List<SubscriptionDto> findByLinkId(long linkId);

    @NotNull SubscriptionDto findByAlias(@NotNull String alias);

    /**
     * Сохраняет сущность в БД.
     * @param subscription новая сущность, id может быть null
     * @return Сохранённая сущность, как она представлена в БД
     */
    @NotNull SubscriptionDto add(@NotNull SubscriptionDto subscription);

    /**
     * Удаляет сущность из БД.
     * @param id значение id этой сущности
     * @return удалённую сущность в БД
     */
    @NotNull SubscriptionDto remove(long id);
}
