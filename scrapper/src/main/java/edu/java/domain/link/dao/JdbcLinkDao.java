package edu.java.domain.link.dao;

import edu.java.domain.link.dto.LinkDto;
import jakarta.validation.constraints.NotNull;
import java.net.URI;
import java.time.OffsetDateTime;
import java.util.List;
import java.util.Optional;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Repository;

@Repository
public class JdbcLinkDao {
    private static final String FIND_ALL_QUERY = "SELECT * FROM link";
    private static final String FIND_BY_ID_QUERY = "SELECT * FROM link WHERE id = ?";
    private static final String FIND_BY_URL_QUERY = "SELECT * FROM link WHERE url = ?";
    private static final String FIND_BY_LAST_CHECK_QUERY = "SELECT * FROM link WHERE last_check <= ?";
    private static final String ADD_QUERY = "SELECT * FROM insert_link(?, ?, ?, ?::jsonb, ?)";
    private static final String UPDATE_QUERY =
        "UPDATE link SET url = ?, service_type = ?, tracked_data = ?::jsonb, last_check = ? WHERE id = ? RETURNING *";
    private static final String REMOVE_QUERY = "DELETE FROM link WHERE id = ? RETURNING *";

    private final JdbcTemplate jdbcTemplate;

    public JdbcLinkDao(JdbcTemplate jdbcTemplate) {
        this.jdbcTemplate = jdbcTemplate;
    }

    @NotNull private static Optional<LinkDto> boxIntoOptional(List<LinkDto> result) {
        return result.stream().findFirst();
    }

    public @NotNull List<LinkDto> findAll() {
        return jdbcTemplate.query(FIND_ALL_QUERY, new LinkDtoRowMapper());
    }

    public @NotNull Optional<LinkDto> findById(long id) {
        var result = jdbcTemplate.query(FIND_BY_ID_QUERY, new LinkDtoRowMapper(), id);
        return boxIntoOptional(result);
    }

    public @NotNull Optional<LinkDto> findByUrl(URI uri) {
        var result = jdbcTemplate.query(FIND_BY_URL_QUERY, new LinkDtoRowMapper(), uri.toString());
        return boxIntoOptional(result);
    }

    /**
     * Возвращает все {@link LinkDto} у которых lastUpdate равен или больше данного.
     * @param lastUpdate значение lastUpdate
     * @return все {@link LinkDto} у которых lastUpdate равен или больше данного
     */
    public @NotNull List<LinkDto> findFromLastUpdate(@NotNull OffsetDateTime lastUpdate) {
        return jdbcTemplate.query(FIND_BY_LAST_CHECK_QUERY, new LinkDtoRowMapper(), lastUpdate);
    }

    /**
     * Сохраняет сущность в БД.
     * @param link новая сущность, id может быть null
     * @return Сохранённая сущность, как она представлена в БД
     */
    public @NotNull LinkDto add(@NotNull LinkDto link) {
        return jdbcTemplate.queryForObject(ADD_QUERY,
            new LinkDtoRowMapper(),
            link.id(),
            link.url().toString(),
            link.serviceType(),
            link.trackedData(),
            link.lastCheck()
        );
    }

    /**
     * Обновляет сущность в БД.
     * Если сущности с таким id не существует, то метод вернёт пустой Optional.
     *
     * @param link обновлённая сущность, должен быть указан id
     * @return обновлённая сущность если обновление завершилось успешно
     */
    public @NotNull Optional<LinkDto> update(@NotNull LinkDto link) {
        var result = jdbcTemplate.query(UPDATE_QUERY,
            new LinkDtoRowMapper(),
            link.url().toString(),
            link.serviceType(),
            link.trackedData(),
            link.lastCheck(),
            link.id()
        );
        return boxIntoOptional(result);
    }

    /**
     * Удаляет сущность из БД.
     * @param id значение id этой сущности
     * @return удалённую сущность в БД
     */
    public @NotNull LinkDto remove(long id) {
        return jdbcTemplate.queryForObject(REMOVE_QUERY, new LinkDtoRowMapper(), id);
    }
}
