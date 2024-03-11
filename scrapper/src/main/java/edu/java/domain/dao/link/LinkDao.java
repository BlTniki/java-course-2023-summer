package edu.java.domain.dao.link;

import edu.java.domain.dto.LinkDto;
import jakarta.validation.constraints.NotNull;
import java.net.URI;
import java.time.OffsetDateTime;
import java.util.List;

public interface LinkDao {
    @NotNull List<LinkDto> findAll();

    @NotNull LinkDto findById(long id);

    @NotNull LinkDto findByUrl(URI uri);

    /**
     * Возвращает все {@link LinkDto} у которых lastUpdate равен или больше данного.
     * @param lastUpdate значение lastUpdate
     * @return все {@link LinkDto} у которых lastUpdate равен или больше данного
     */
    @NotNull List<LinkDto> findFromLastUpdate(OffsetDateTime lastUpdate);

    /**
     * Сохраняет сущность в БД.
     * @param link новая сущность
     * @return Сохранённая сущность, как она представлена в БД
     */
    @NotNull LinkDto add(@NotNull LinkDto link);

    /**
     * Удаляет сущность из БД.
     * @param id значение id этой сущности
     * @return удалённую сущность в БД
     */
    @NotNull LinkDto remove(long id);
}
