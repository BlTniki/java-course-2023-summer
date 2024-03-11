package edu.java.domain.dao.subscription;

import edu.java.domain.dao.rowMapper.SubscriptionDtoRowMapper;
import edu.java.domain.dto.SubscriptionDto;
import java.util.List;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;


@Repository
@Transactional
public class JdbcSubscriptionDao implements SubscriptionDao {
    private static final String FIND_ALL_QUERY = "SELECT * FROM subscription";
    private static final String FIND_BY_ID_QUERY = "SELECT * FROM subscription WHERE id = ?";
    private static final String FIND_BY_CHAT_ID_QUERY = "SELECT * FROM subscription WHERE chat_id = ?";
    private static final String FIND_BY_LINK_ID_QUERY = "SELECT * FROM subscription WHERE link_id = ?";
    private static final String FIND_BY_ALIAS_QUERY = "SELECT * FROM subscription WHERE alias = ?";
    private static final String ADD_WITH_ID_QUERY =
        "INSERT INTO subscription (id, chat_id, link_id, alias) VALUES (?, ?, ?, ?) RETURNING *";
    private static final String ADD_WITHOUT_ID_QUERY =
        "INSERT INTO subscription (chat_id, link_id, alias) VALUES (?, ?, ?) RETURNING *";
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
    public SubscriptionDto findById(long id) {
        return jdbcTemplate.queryForObject(FIND_BY_ID_QUERY, new SubscriptionDtoRowMapper(), id);
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
    public SubscriptionDto findByAlias(String alias) {
        return jdbcTemplate.queryForObject(FIND_BY_ALIAS_QUERY, new SubscriptionDtoRowMapper(), alias);
    }

    @Override
    public SubscriptionDto add(SubscriptionDto subscription) {
        if (subscription.id() == null) {
            return jdbcTemplate.queryForObject(ADD_WITHOUT_ID_QUERY,
                new SubscriptionDtoRowMapper(),
                subscription.chatId(),
                subscription.linkId(),
                subscription.alias()
            );
        }
        return jdbcTemplate.queryForObject(ADD_WITH_ID_QUERY,
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
