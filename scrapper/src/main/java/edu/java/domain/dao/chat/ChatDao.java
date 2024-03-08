package edu.java.domain.dao.chat;

import edu.java.domain.dto.ChatDto;
import jakarta.validation.constraints.NotNull;
import java.util.List;

public interface ChatDao {
    @NotNull ChatDto findById(long id);

    @NotNull List<ChatDto> findAll();

    /**
     * Сохраняет сущность в БД.
     * @param chat новая сущность
     * @return Сохранённая сущность, как она представлена в БД
     */
    @NotNull ChatDto add(@NotNull ChatDto chat);

    /**
     * Удаляет сущность из БД.
     * @param id значение id этой сущности
     * @return удалённую сущность в БД
     */
    @NotNull ChatDto remove(long id);
}
