package edu.java.domain.link.dao;

import edu.java.domain.link.dto.SubscriptionDto;
import jakarta.validation.constraints.NotNull;
import java.util.List;
import java.util.Optional;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Repository;


@Repository
public class JdbcSubscriptionDao {
    private static final String FIND_ALL_QUERY = "SELECT * FROM subscription";
    private static final String FIND_BY_ID_QUERY = "SELECT * FROM subscription WHERE id = ?";
    private static final String FIND_BY_CHAT_ID_QUERY = "SELECT * FROM subscription WHERE chat_id = ?";
    private static final String FIND_BY_LINK_ID_QUERY = "SELECT * FROM subscription WHERE link_id = ?";
    private static final String FIND_BY_ALIAS_QUERY = "SELECT * FROM subscription WHERE chat_id = ? AND alias = ?";
    private static final String ADD_QUERY = "SELECT * FROM insert_subscription(?, ?, ?, ?)";
    private static final String REMOVE_QUERY = "DELETE FROM subscription WHERE id = ? RETURNING *";

    private final JdbcTemplate jdbcTemplate;

    public JdbcSubscriptionDao(JdbcTemplate jdbcTemplate) {
        this.jdbcTemplate = jdbcTemplate;
    }

    public @NotNull List<SubscriptionDto> findAll() {
        return jdbcTemplate.query(FIND_ALL_QUERY, new SubscriptionDtoRowMapper());
    }

    public @NotNull Optional<SubscriptionDto> findById(long id) {
        var result = jdbcTemplate.query(FIND_BY_ID_QUERY, new SubscriptionDtoRowMapper(), id);
        return result.stream().findFirst();
    }

    public @NotNull List<SubscriptionDto> findByChatId(long chatId) {
        return jdbcTemplate.query(FIND_BY_CHAT_ID_QUERY, new SubscriptionDtoRowMapper(), chatId);
    }

    public @NotNull List<SubscriptionDto> findByLinkId(long linkId) {
        return jdbcTemplate.query(FIND_BY_LINK_ID_QUERY, new SubscriptionDtoRowMapper(), linkId);
    }

    public @NotNull Optional<SubscriptionDto> findByChatIdAndAlias(long chatId, String alias) {
        var result = jdbcTemplate.query(FIND_BY_ALIAS_QUERY, new SubscriptionDtoRowMapper(), chatId, alias);
        return result.stream().findFirst();
    }

    /**
     * Сохраняет сущность в БД.
     * @param subscription новая сущность, id может быть null
     * @return Сохранённая сущность, как она представлена в БД
     */
    public @NotNull SubscriptionDto add(@NotNull SubscriptionDto subscription) {
        return jdbcTemplate.queryForObject(ADD_QUERY,
            new SubscriptionDtoRowMapper(),
            subscription.id(),
            subscription.chatId(),
            subscription.linkId(),
            subscription.alias()
        );
    }

    /**
     * Удаляет сущность из БД.
     * @param id значение id этой сущности
     * @return удалённую сущность в БД
     */
    public @NotNull SubscriptionDto remove(long id) {
        return jdbcTemplate.queryForObject(REMOVE_QUERY, new SubscriptionDtoRowMapper(), id);
    }
}
