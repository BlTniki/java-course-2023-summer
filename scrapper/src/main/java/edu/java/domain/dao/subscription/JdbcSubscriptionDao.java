package edu.java.domain.dao.subscription;

import edu.java.domain.dao.rowMapper.SubscriptionDtoRowMapper;
import edu.java.domain.dto.SubscriptionDto;
import java.util.List;
import java.util.Optional;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Repository;


@Repository
public class JdbcSubscriptionDao implements SubscriptionDao {
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

    @Override
    public List<SubscriptionDto> findAll() {
        return jdbcTemplate.query(FIND_ALL_QUERY, new SubscriptionDtoRowMapper());
    }

    @Override
    public Optional<SubscriptionDto> findById(long id) {
        var result = jdbcTemplate.query(FIND_BY_ID_QUERY, new SubscriptionDtoRowMapper(), id);
        return result.stream().findFirst();
    }

    @Override
    public List<SubscriptionDto> findByChatId(long chatId) {
        return jdbcTemplate.query(FIND_BY_CHAT_ID_QUERY, new SubscriptionDtoRowMapper(), chatId);
    }

    @Override
    public List<SubscriptionDto> findByLinkId(long linkId) {
        return jdbcTemplate.query(FIND_BY_LINK_ID_QUERY, new SubscriptionDtoRowMapper(), linkId);
    }

    @Override
    public Optional<SubscriptionDto> findByChatIdAndAlias(long chatId, String alias) {
        var result = jdbcTemplate.query(FIND_BY_ALIAS_QUERY, new SubscriptionDtoRowMapper(), chatId, alias);
        return result.stream().findFirst();
    }

    @Override
    public SubscriptionDto add(SubscriptionDto subscription) {
        return jdbcTemplate.queryForObject(ADD_QUERY,
            new SubscriptionDtoRowMapper(),
            subscription.id(),
            subscription.chatId(),
            subscription.linkId(),
            subscription.alias()
        );
    }

    @Override
    public SubscriptionDto remove(long id) {
        return jdbcTemplate.queryForObject(REMOVE_QUERY, new SubscriptionDtoRowMapper(), id);
    }
}
