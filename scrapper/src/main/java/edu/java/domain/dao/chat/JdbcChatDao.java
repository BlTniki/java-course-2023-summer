package edu.java.domain.dao.chat;

import edu.java.domain.dao.rowMapper.ChatDtoRowMapper;
import edu.java.domain.dto.ChatDto;
import java.util.List;
import java.util.Optional;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Repository;

@Repository
public class JdbcChatDao implements ChatDao {
    private static final String FIND_BY_ID_QUERY = "SELECT * FROM chat WHERE id = ?";
    private static final String FIND_ALL_QUERY = "SELECT * FROM chat";
    private static final String ADD_QUERY = "INSERT INTO chat (id) VALUES (?) RETURNING *";
    private static final String REMOVE_QUERY = "DELETE FROM chat WHERE id = ? RETURNING *";

    private final JdbcTemplate jdbcTemplate;

    public JdbcChatDao(JdbcTemplate jdbcTemplate) {
        this.jdbcTemplate = jdbcTemplate;
    }

    @Override
    public Optional<ChatDto> findById(long id) {
        var result = jdbcTemplate.query(FIND_BY_ID_QUERY, new ChatDtoRowMapper(), id);
        return result.stream().findFirst();
    }

    @Override
    public List<ChatDto> findAll() {
        return jdbcTemplate.query(FIND_ALL_QUERY, new ChatDtoRowMapper());
    }

    @Override
    public ChatDto add(ChatDto chat) {
        return jdbcTemplate.queryForObject(ADD_QUERY, new ChatDtoRowMapper(), chat.id());
    }

    @Override
    public ChatDto remove(long id) {
        return jdbcTemplate.queryForObject(REMOVE_QUERY, new ChatDtoRowMapper(), id);
    }
}
