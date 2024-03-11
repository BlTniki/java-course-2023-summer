package edu.java.domain.dao.rowMapper;

import edu.java.domain.dto.LinkDto;
import java.net.URI;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.time.OffsetDateTime;
import org.springframework.jdbc.core.RowMapper;

public class LinkDtoRowMapper implements RowMapper<LinkDto> {
    @Override
    public LinkDto mapRow(ResultSet rs, int rowNum) throws SQLException {
        var id = rs.getLong("id");
        URI url = rs.getObject("url", URI.class);
        OffsetDateTime lastUpdate = rs.getObject("last_update", OffsetDateTime.class);
        return new LinkDto(id, url, lastUpdate);
    }
}
