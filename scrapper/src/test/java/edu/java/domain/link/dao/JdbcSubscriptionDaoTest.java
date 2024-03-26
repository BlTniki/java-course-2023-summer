package edu.java.domain.link.dao;

import edu.java.ScrapperApplicationTests;
import edu.java.domain.chat.dao.JdbcChatDao;
import edu.java.domain.chat.dto.ChatDto;
import edu.java.domain.link.dto.LinkDto;
import edu.java.domain.link.dto.SubscriptionDto;
import java.net.URI;
import java.time.OffsetDateTime;
import java.util.List;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.test.annotation.Rollback;
import org.springframework.transaction.annotation.Transactional;
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

        jdbcLinkDao.add(new LinkDto(1L, URI.create("http://example.com/1"), "lol", "{}", OffsetDateTime.now()));

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
        jdbcChatDao.add(new ChatDto(1L));
        jdbcLinkDao.add(new LinkDto(1L, URI.create("http://example.com/1"), "lol", "{}", OffsetDateTime.now()));

        var expected = jdbcSubscriptionDao.add(new SubscriptionDto(1L, 1L, 1L, "1"));

        var actual = jdbcSubscriptionDao.findById(expected.id());

        assertThat(actual)
            .isPresent()
            .contains(expected);
    }

    @Test
    @DisplayName("Проверим что мы не ломаемся если id не существует")
    @Rollback
    void findById_notExist() {
        var actual = jdbcSubscriptionDao.findById(1L);

        assertThat(actual)
            .isNotPresent();
    }

    @Test
    @DisplayName("Проверим, что мы можем найти ссылку по ChatId")
    @Rollback
    void findByChatId() {
        jdbcChatDao.add(new ChatDto(1L));
        jdbcChatDao.add(new ChatDto(2L));
        jdbcChatDao.add(new ChatDto(3L));

        jdbcLinkDao.add(new LinkDto(1L, URI.create("http://example.com/1"), "lol", "{}", OffsetDateTime.now()));

        var expected = List.of(
            new SubscriptionDto(1L, 1L, 1L, "1"),
            new SubscriptionDto(2L, 2L, 1L, "1"),
            new SubscriptionDto(3L, 3L, 1L, "1")
        );
        for (var subscription: expected) {
            jdbcSubscriptionDao.add(subscription);
        }

        var actual = jdbcSubscriptionDao.findByChatId(1L);

        assertThat(actual)
            .containsExactlyInAnyOrderElementsOf(List.of(expected.getFirst()));
    }

    @Test
    @DisplayName("Проверим что мы не ломаемся если ChatId не существует")
    @Rollback
    void findByChatId_notExist() {
        var actual = jdbcSubscriptionDao.findByChatId(1L);

        assertThat(actual)
            .isEmpty();
    }

    @Test
    @DisplayName("Проверим, что мы можем найти ссылку по LinkId")
    @Rollback
    void findByLinkId() {
        jdbcChatDao.add(new ChatDto(1L));
        jdbcChatDao.add(new ChatDto(2L));
        jdbcChatDao.add(new ChatDto(3L));

        jdbcLinkDao.add(new LinkDto(1L, URI.create("http://example.com/1"), "lol", "{}", OffsetDateTime.now()));

        var expected = List.of(
            new SubscriptionDto(1L, 1L, 1L, "1"),
            new SubscriptionDto(2L, 2L, 1L, "1"),
            new SubscriptionDto(3L, 3L, 1L, "1")
        );
        for (var subscription: expected) {
            jdbcSubscriptionDao.add(subscription);
        }

        var actual = jdbcSubscriptionDao.findByLinkId(1L);

        assertThat(actual)
            .containsExactlyInAnyOrderElementsOf(expected);
    }

    @Test
    @DisplayName("Проверим что мы не ломаемся если LinkId не существует")
    @Rollback
    void findByLinkId_notExist() {
        var actual = jdbcSubscriptionDao.findByLinkId(1L);

        assertThat(actual)
            .isEmpty();
    }

    @Test
    @DisplayName("Проверим, что мы можем найти ссылку по Alias")
    @Rollback
    void findByChatIdAndAlias() {
        jdbcChatDao.add(new ChatDto(1L));
        jdbcChatDao.add(new ChatDto(2L));
        jdbcChatDao.add(new ChatDto(3L));

        jdbcLinkDao.add(new LinkDto(1L, URI.create("http://example.com/1"), "lol", "{}", OffsetDateTime.now()));

        var expected = List.of(
            new SubscriptionDto(1L, 1L, 1L, "1"),
            new SubscriptionDto(2L, 2L, 1L, "1"),
            new SubscriptionDto(3L, 3L, 1L, "1")
        );
        for (var subscription: expected) {
            jdbcSubscriptionDao.add(subscription);
        }

        var actual = jdbcSubscriptionDao.findByChatIdAndAlias(1L, "1");

        assertThat(actual)
            .isPresent()
            .contains(expected.getFirst());
    }

    @Test
    @DisplayName("Проверим что мы не ломаемся если Alias не существует")
    @Rollback
    void findByChatIdAndAlias_notExist() {
        var actual = jdbcSubscriptionDao.findByChatIdAndAlias(1L, "1");

        assertThat(actual)
            .isEmpty();
    }

    @Test
    @DisplayName("Проверим что запись работает если id задан")
    @Rollback
    void add() {
        jdbcChatDao.add(new ChatDto(1L));
        jdbcLinkDao.add(new LinkDto(1L, URI.create("http://example.com/1"), "lol", "{}", OffsetDateTime.now()));

        var expected = new SubscriptionDto(1L, 1L, 1L, "1");

        var actual = jdbcSubscriptionDao.add(expected);

        assertThat(actual).isEqualTo(expected);
    }

    @Test
    @DisplayName("Проверим что запись работает если id не задан")
    @Rollback
    void add_no_id() {
        jdbcChatDao.add(new ChatDto(1L));
        jdbcLinkDao.add(new LinkDto(1L, URI.create("http://example.com/1"), "lol", "{}", OffsetDateTime.now()));


        var actual = jdbcSubscriptionDao.add(new SubscriptionDto(null, 1L, 1L, "1"));

        assertThat(actual.id()).isNotNull();
    }

    @Test
    @DisplayName("Проверим что запись удаляется")
    @Rollback
    void remove() {
        jdbcChatDao.add(new ChatDto(1L));
        jdbcLinkDao.add(new LinkDto(1L, URI.create("http://example.com/1"), "lol", "{}", OffsetDateTime.now()));

        jdbcSubscriptionDao.add(new SubscriptionDto(1L, 1L, 1L, "1"));

        jdbcSubscriptionDao.remove(1L);

        var actual = jdbcSubscriptionDao.findAll();

        assertThat(actual).isEmpty();
    }
}
