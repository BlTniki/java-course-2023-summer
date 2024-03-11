package edu.java.domain.dao.link;

import edu.java.domain.dao.rowMapper.LinkDtoRowMapper;
import edu.java.domain.dto.LinkDto;
import java.net.URI;
import java.time.OffsetDateTime;
import java.util.List;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;

@Repository
@Transactional
public class JdbcLinkDao implements LinkDao {
    private static final String FIND_ALL_QUERY = "SELECT * FROM link";
    private static final String FIND_BY_ID_QUERY = "SELECT * FROM link WHERE id = ?";
    private static final String FIND_BY_URL_QUERY = "SELECT * FROM link WHERE url = ?";
    private static final String FIND_BY_LAST_UPDATE_QUERY = "SELECT * FROM link WHERE last_update >= ?";
    private static final String ADD_QUERY = "INSERT INTO link (url, last_update) VALUES (?, ?) RETURNING *";
    private static final String REMOVE_QUERY = "DELETE FROM link WHERE id = ? RETURNING *";

    private final JdbcTemplate jdbcTemplate;

    public JdbcLinkDao(JdbcTemplate jdbcTemplate) {
        this.jdbcTemplate = jdbcTemplate;
    }

    @Override
    public List<LinkDto> findAll() {
        return jdbcTemplate.query(FIND_ALL_QUERY, new LinkDtoRowMapper());
    }

    @Override
    public LinkDto findById(long id) {
        return jdbcTemplate.queryForObject(FIND_BY_ID_QUERY, new LinkDtoRowMapper(), id);
    }

    @Override
    public LinkDto findByUrl(URI uri) {
        return jdbcTemplate.queryForObject(FIND_BY_ID_QUERY, new LinkDtoRowMapper(), uri);
    }

    @Override
    public List<LinkDto> findFromLastUpdate(OffsetDateTime lastUpdate) {
        return jdbcTemplate.query(FIND_BY_LAST_UPDATE_QUERY, new LinkDtoRowMapper(), lastUpdate);
    }

    @Override
    public LinkDto add(LinkDto link) {
        return jdbcTemplate.queryForObject(ADD_QUERY, new LinkDtoRowMapper(), link.url(), link.lastUpdate());
    }

    @Override
    public LinkDto remove(long id) {
        return jdbcTemplate.queryForObject(REMOVE_QUERY, new LinkDtoRowMapper(), id);
    }
}
