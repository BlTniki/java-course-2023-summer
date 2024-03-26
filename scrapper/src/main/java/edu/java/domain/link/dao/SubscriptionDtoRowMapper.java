package edu.java.domain.link.dao;

import edu.java.domain.link.dto.SubscriptionDto;
import java.sql.ResultSet;
import java.sql.SQLException;
import org.springframework.jdbc.core.RowMapper;

public class SubscriptionDtoRowMapper implements RowMapper<SubscriptionDto> {
    @Override
    public SubscriptionDto mapRow(ResultSet rs, int rowNum) throws SQLException {
        var id = rs.getLong("id");
        var chatId = rs.getLong("chat_id");
        var linkId = rs.getLong("link_id");
        var alias = rs.getString("alias");
        return new SubscriptionDto(id, chatId, linkId, alias);
    }
}
