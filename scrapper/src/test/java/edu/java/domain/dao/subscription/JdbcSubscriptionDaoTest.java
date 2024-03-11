package edu.java.domain.dao.subscription;

import edu.java.ScrapperApplicationTests;
import edu.java.domain.dao.chat.JdbcChatDao;
import edu.java.domain.dao.link.JdbcLinkDao;
import edu.java.domain.dto.ChatDto;
import edu.java.domain.dto.LinkDto;
import edu.java.domain.dto.SubscriptionDto;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.test.annotation.Rollback;
import org.springframework.transaction.annotation.Transactional;
import java.net.URI;
import java.time.OffsetDateTime;
import java.util.List;
import static org.assertj.core.api.Assertions.assertThat;

@Transactional
class JdbcSubscriptionDaoTest extends ScrapperApplicationTests {
    @Autowired
    private JdbcSubscriptionDao jdbcSubscriptionDao;
    @Autowired
    private JdbcChatDao jdbcChatDao;
    @Autowired
    private JdbcLinkDao jdbcLinkDao;

    @Test
    @DisplayName("Проверим, что мы получаем все ссылки")
    @Rollback
    void findAll() {
        jdbcChatDao.add(new ChatDto(1L));
        jdbcChatDao.add(new ChatDto(2L));
        jdbcChatDao.add(new ChatDto(3L));

        jdbcLinkDao.add(new LinkDto(1L, URI.create("http://example.com/1"), OffsetDateTime.now()));

        var expected = List.of(
            new SubscriptionDto(1L, 1L, 1L, "1"),
            new SubscriptionDto(2L, 2L, 1L, "1"),
            new SubscriptionDto(3L, 3L, 1L, "1")
        );
        for (var subscription: expected) {
            jdbcSubscriptionDao.add(subscription);
        }

        var actual = jdbcSubscriptionDao.findAll();

        assertThat(actual)
            .containsExactlyInAnyOrderElementsOf(expected);
    }

    @Test
    @DisplayName("Проверим, что мы можем найти ссылку по id")
    @Rollback
    void findById() {
    }

    @Test
    @DisplayName("Проверим, что мы можем найти ссылку по id")
    @Rollback
    void findByChatId() {
    }

    @Test
    @DisplayName("Проверим, что мы можем найти ссылку по id")
    @Rollback
    void findByLinkId() {
    }

    @Test
    @DisplayName("Проверим, что мы можем найти ссылку по id")
    @Rollback
    void findByAlias() {
    }

    @Test
    @DisplayName("Проверим что запись работает если id не задан")
    @Rollback
    void add() {
    }

    @Test
    @DisplayName("Проверим что запись удаляется")
    @Rollback
    void remove() {
    }
}
