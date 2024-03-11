package edu.java.domain.dao.link;


import edu.java.ScrapperApplicationTests;
import edu.java.domain.dto.LinkDto;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.test.annotation.Rollback;
import org.springframework.transaction.annotation.Transactional;
import java.net.URI;
import java.time.OffsetDateTime;
import java.time.ZoneOffset;
import java.time.temporal.ChronoUnit;
import java.util.List;
import static org.assertj.core.api.Assertions.assertThat;

@Transactional
class JdbcLinkDaoTest extends ScrapperApplicationTests {
    @Autowired
    private JdbcLinkDao jdbcLinkDao;

    @Test
    @DisplayName("Проверим, что мы получаем все ссылки")
    @Rollback
    void findAll() {
        var expected = List.of(
            new LinkDto(1L, URI.create("http://example.com/1"), OffsetDateTime.now(ZoneOffset.UTC).truncatedTo(ChronoUnit.SECONDS)),
            new LinkDto(2L, URI.create("http://example.com/2"), OffsetDateTime.now(ZoneOffset.UTC).truncatedTo(ChronoUnit.SECONDS)),
            new LinkDto(3L, URI.create("http://example.com/3"), OffsetDateTime.now(ZoneOffset.UTC).truncatedTo(ChronoUnit.SECONDS))
        );
        for (var link: expected) {
            jdbcLinkDao.add(link);
        }

        var actual = jdbcLinkDao.findAll();

        assertThat(actual)
            .containsExactlyInAnyOrderElementsOf(expected);
    }

    @Test
    @DisplayName("Проверим, что мы можем найти ссылку по id")
    @Rollback
    void findById() {
        var expectedDto = new LinkDto(1L, URI.create("http://example.com"), OffsetDateTime.now(ZoneOffset.UTC).truncatedTo(ChronoUnit.SECONDS));
        jdbcLinkDao.add(expectedDto);

        var actualDto = jdbcLinkDao.findById(expectedDto.id());

        assertThat(actualDto.id()).isEqualTo(expectedDto.id());
        assertThat(actualDto.url()).isEqualTo(expectedDto.url());
        assertThat(actualDto.lastUpdate()).isEqualTo(expectedDto.lastUpdate());
    }

    @Test
    @DisplayName("Проверим, что мы можем найти ссылку по url")
    @Rollback
    void findByUrl() {
        var expectedDto = new LinkDto(1L, URI.create("http://example.com"), OffsetDateTime.now(ZoneOffset.UTC).truncatedTo(ChronoUnit.SECONDS));
        jdbcLinkDao.add(expectedDto);

        var actualDto = jdbcLinkDao.findByUrl(expectedDto.url());

        assertThat(actualDto).isEqualTo(expectedDto);
    }

    @Test
    @DisplayName("Проверим, что мы можем найти ссылку по LastUpdate")
    @Rollback
    void findFromLastUpdate() {
        var lastUpdate = OffsetDateTime.now(ZoneOffset.UTC).truncatedTo(ChronoUnit.SECONDS).minusDays(1);
        var expectedDto = new LinkDto(1L, URI.create("http://example.com"), OffsetDateTime.now(ZoneOffset.UTC).truncatedTo(ChronoUnit.SECONDS));
        jdbcLinkDao.add(expectedDto);

        var actualList = jdbcLinkDao.findFromLastUpdate(lastUpdate);

        assertThat(actualList).containsExactly(expectedDto);
    }

    @Test
    @DisplayName("Проверим что запись работает если задан id")
    @Rollback
    void add() {
        var expectedDto = new LinkDto(1L, URI.create("http://example.com"), OffsetDateTime.now(ZoneOffset.UTC).truncatedTo(ChronoUnit.SECONDS));

        var actualDto = jdbcLinkDao.add(expectedDto);

        assertThat(actualDto.lastUpdate().isEqual(expectedDto.lastUpdate())).isTrue();
    }

    @Test
    @DisplayName("Проверим что запись работает если id не задан")
    @Rollback
    void add_no_id() {
        var expectedDto = new LinkDto(1L, URI.create("http://example.com"), OffsetDateTime.now(ZoneOffset.UTC).truncatedTo(ChronoUnit.SECONDS));

        var actualDto = jdbcLinkDao.add(new LinkDto(null, URI.create("http://example.com"), OffsetDateTime.now(ZoneOffset.UTC).truncatedTo(ChronoUnit.SECONDS)));

        assertThat(actualDto.lastUpdate().isEqual(expectedDto.lastUpdate())).isTrue();
    }

    @Test
    @DisplayName("Проверим что запись удаляется")
    @Rollback
    void remove() {
        jdbcLinkDao.add(new LinkDto(1L, URI.create("http://example.com"), OffsetDateTime.now(ZoneOffset.UTC).truncatedTo(ChronoUnit.SECONDS)));

        jdbcLinkDao.remove(1L);

        assertThat(jdbcLinkDao.findAll()).isEmpty();
    }
}
