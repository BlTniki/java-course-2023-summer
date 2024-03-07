package edu.java.domain.dao;

import edu.java.ScrapperApplicationTests;
import edu.java.domain.dto.ChatDto;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.test.annotation.Rollback;
import org.springframework.transaction.annotation.Transactional;
import static org.assertj.core.api.Assertions.assertThat;

@Transactional
class JdbcChatDaoTest extends ScrapperApplicationTests {
    @Autowired
    private JdbcChatDao chatDao;

    @Test
    @DisplayName("Проверим что мы можем найти чат по id")
    @Rollback
    void findById() {
        var expectedDto = new ChatDto(1L);
        chatDao.add(expectedDto);

        var actualDto = chatDao.findById(expectedDto.id());

        assertThat(actualDto).isEqualTo(expectedDto);
    }

    @Test
    void findAll() {
    }

    @Test
    void add() {
    }

    @Test
    void remove() {
    }
}
