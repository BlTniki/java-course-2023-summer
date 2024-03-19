package edu.java.domain.dao.link;

import edu.java.domain.dao.rowMapper.LinkDtoRowMapper;
import edu.java.domain.dto.LinkDto;
import java.net.URI;
import java.time.OffsetDateTime;
import java.util.List;
import java.util.Optional;
import org.jetbrains.annotations.NotNull;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Repository;

@Repository
public class JdbcLinkDao implements LinkDao {
    private static final String FIND_ALL_QUERY = "SELECT * FROM link";
    private static final String FIND_BY_ID_QUERY = "SELECT * FROM link WHERE id = ?";
    private static final String FIND_BY_URL_QUERY = "SELECT * FROM link WHERE url = ?";
    private static final String FIND_BY_LAST_CHECK_QUERY = "SELECT * FROM link WHERE last_check >= ?";
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

    @Override
    public List<LinkDto> findAll() {
        return jdbcTemplate.query(FIND_ALL_QUERY, new LinkDtoRowMapper());
    }

    @Override
    public Optional<LinkDto> findById(long id) {
        var result = jdbcTemplate.query(FIND_BY_ID_QUERY, new LinkDtoRowMapper(), id);
        return boxIntoOptional(result);
    }

    @Override
    public Optional<LinkDto> findByUrl(URI uri) {
        var result = jdbcTemplate.query(FIND_BY_URL_QUERY, new LinkDtoRowMapper(), uri.toString());
        return boxIntoOptional(result);
    }

    @Override
    public List<LinkDto> findFromLastUpdate(OffsetDateTime lastUpdate) {
        return jdbcTemplate.query(FIND_BY_LAST_CHECK_QUERY, new LinkDtoRowMapper(), lastUpdate);
    }

    @Override
    public LinkDto add(LinkDto link) {
        return jdbcTemplate.queryForObject(ADD_QUERY,
            new LinkDtoRowMapper(),
            link.id(),
            link.url().toString(),
            link.serviceType(),
            link.trackedData(),
            link.lastCheck()
        );
    }

    @Override
    public Optional<LinkDto> update(LinkDto link) {
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

    @Override
    public LinkDto remove(long id) {
        return jdbcTemplate.queryForObject(REMOVE_QUERY, new LinkDtoRowMapper(), id);
    }
}
