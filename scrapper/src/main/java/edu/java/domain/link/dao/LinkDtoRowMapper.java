package edu.java.domain.link.dao;

import edu.java.domain.link.dto.LinkDto;
import java.net.URI;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.time.OffsetDateTime;
import org.springframework.jdbc.core.RowMapper;

public class LinkDtoRowMapper implements RowMapper<LinkDto> {
    @Override
    public LinkDto mapRow(ResultSet rs, int rowNum) throws SQLException {
        var id = rs.getLong("id");
        URI url = URI.create(rs.getString("url"));
        String serviceType = rs.getString("service_type");
        String trackedData = rs.getString("tracked_data");
        OffsetDateTime lastCheck = rs.getObject("last_check", OffsetDateTime.class);
        return new LinkDto(id, url, serviceType, trackedData, lastCheck);
    }
}
