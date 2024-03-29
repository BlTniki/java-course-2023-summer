package edu.java.domain.chat.dao;

import edu.java.domain.chat.dto.ChatDto;
import java.sql.ResultSet;
import java.sql.SQLException;
import org.springframework.jdbc.core.RowMapper;

public class ChatDtoRowMapper implements RowMapper<ChatDto> {
    @Override
    public ChatDto mapRow(ResultSet rs, int rowNum) throws SQLException {
        Long id = rs.getLong("id");

        return new ChatDto(id);
    }
}
