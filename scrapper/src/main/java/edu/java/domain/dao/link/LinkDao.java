package edu.java.domain.dao.link;

import edu.java.domain.dto.LinkDto;
import jakarta.validation.constraints.NotNull;
import java.net.URI;
import java.time.OffsetDateTime;
import java.util.List;
import java.util.Optional;

public interface LinkDao {
    @NotNull List<LinkDto> findAll();

    @NotNull Optional<LinkDto> findById(long id);

    @NotNull Optional<LinkDto> findByUrl(URI uri);

    /**
     * Возвращает все {@link LinkDto} у которых lastUpdate равен или больше данного.
     * @param lastUpdate значение lastUpdate
     * @return все {@link LinkDto} у которых lastUpdate равен или больше данного
     */
    @NotNull List<LinkDto> findFromLastUpdate(OffsetDateTime lastUpdate);

    /**
     * Сохраняет сущность в БД.
     * @param link новая сущность, id может быть null
     * @return Сохранённая сущность, как она представлена в БД
     */
    @NotNull LinkDto add(@NotNull LinkDto link);

    /**
     * Обновляет сущность в БД.
     * Если сущности с таким id не существует, то метод вернёт пустой Optional.
     *
     * @param linkDto обновлённая сущность, должен быть указан id
     * @return обновлённая сущность если обновление завершилось успешно
     */
    Optional<LinkDto> update(@NotNull LinkDto linkDto);

    /**
     * Удаляет сущность из БД.
     * @param id значение id этой сущности
     * @return удалённую сущность в БД
     */
    @NotNull LinkDto remove(long id);
}
