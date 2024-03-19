package edu.java.domain.dao.chat;

import edu.java.ScrapperApplicationTests;
import edu.java.domain.dto.ChatDto;
import java.util.List;
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

        assertThat(actualDto)
            .isPresent()
            .contains(expectedDto);
    }

    @Test
    @DisplayName("Проверим что мы не ломаемся если id не существует")
    @Rollback
    void findById_notExist() {

        var actualDto = chatDao.findById(1L);

        assertThat(actualDto)
            .isNotPresent();
    }

    @Test
    @DisplayName("Проверим что мы получаем все чаты")
    @Rollback
    void findAll() {
        var expected = List.of(
            new ChatDto(1L),
            new ChatDto(2L),
            new ChatDto(3L)
        );
        for (var chat: expected) {
            chatDao.add(chat);
        }

        var actual = chatDao.findAll();

        assertThat(actual)

            .containsExactlyInAnyOrderElementsOf(expected);
    }

    @Test
    @DisplayName("Проверим что запись работает")
    @Rollback
    void add() {
        var expectedDto = new ChatDto(1L);

        var actualDto = chatDao.add(expectedDto);

        assertThat(actualDto).isEqualTo(expectedDto);
    }

    @Test
    @DisplayName("Проверим что запись удаляется")
    @Rollback
    void remove() {
        chatDao.add(new ChatDto(1L));

        chatDao.remove(1L);

        assertThat(chatDao.findAll()).isEmpty();
    }
}
