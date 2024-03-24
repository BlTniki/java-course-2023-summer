package edu.java.domain.chat.dao;

import edu.java.domain.chat.dto.ChatDto;
import jakarta.validation.constraints.NotNull;
import java.util.List;
import java.util.Optional;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Repository;

@Repository
public class JdbcChatDao {
    private static final String FIND_BY_ID_QUERY = "SELECT * FROM chat WHERE id = ?";
    private static final String FIND_ALL_QUERY = "SELECT * FROM chat";
    private static final String ADD_QUERY = "INSERT INTO chat (id) VALUES (?) RETURNING *";
    private static final String REMOVE_QUERY = "DELETE FROM chat WHERE id = ? RETURNING *";

    private final JdbcTemplate jdbcTemplate;

    public JdbcChatDao(JdbcTemplate jdbcTemplate) {
        this.jdbcTemplate = jdbcTemplate;
    }

    public @NotNull Optional<ChatDto> findById(long id) {
        var result = jdbcTemplate.query(FIND_BY_ID_QUERY, new ChatDtoRowMapper(), id);
        return result.stream().findFirst();
    }

    public @NotNull List<ChatDto> findAll() {
        return jdbcTemplate.query(FIND_ALL_QUERY, new ChatDtoRowMapper());
    }

    /**
     * Сохраняет сущность в БД.
     * @param chat новая сущность
     * @return Сохранённая сущность, как она представлена в БД
     */

    public @NotNull ChatDto add(@NotNull ChatDto chat) {
        return jdbcTemplate.queryForObject(ADD_QUERY, new ChatDtoRowMapper(), chat.id());
    }

    /**
     * Удаляет сущность из БД.
     * @param id значение id этой сущности
     * @return удалённую сущность в БД
     */
    public @NotNull ChatDto remove(long id) {
        return jdbcTemplate.queryForObject(REMOVE_QUERY, new ChatDtoRowMapper(), id);
    }
}
