package edu.java.domain.link.service;

import edu.java.ScrapperApplicationTests;
import edu.java.controller.model.AddLinkRequest;
import edu.java.controller.model.RemoveLinkRequest;
import edu.java.domain.chat.dao.JdbcChatDao;
import edu.java.domain.chat.dto.ChatDto;
import edu.java.domain.exception.EntityAlreadyExistException;
import edu.java.domain.exception.EntityNotFoundException;
import edu.java.domain.link.dao.JdbcLinkDao;
import edu.java.domain.link.dao.JdbcSubscriptionDao;
import edu.java.domain.link.dto.Link;
import edu.java.domain.link.dto.LinkDescriptor;
import edu.java.domain.link.dto.LinkDto;
import edu.java.domain.link.dto.LinkUpdateDto;
import edu.java.domain.link.dto.ServiceType;
import edu.java.domain.link.dto.SubscriptionDto;
import edu.java.domain.link.service.github.GitHubLinkChecker;
import edu.java.domain.link.service.stackoverflow.StackOverflowLinkChecker;
import java.net.URI;
import java.time.OffsetDateTime;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.TestConfiguration;
import org.springframework.context.annotation.Bean;
import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

class JdbcLinkServiceTest extends ScrapperApplicationTests {
    @TestConfiguration
    static class Config {
        @Bean
        public JdbcChatDao chatDao() {
            return mock(JdbcChatDao.class);
        }

        @Bean
        public JdbcLinkDao linkDao() {
            return mock(JdbcLinkDao.class);
        }

        @Bean
        public JdbcSubscriptionDao subscriptionDao() {
            return mock(JdbcSubscriptionDao.class);
        }

        @Bean
        public LinkParser linkParser() {
            return mock(LinkParser.class);
        }

        @Bean
        public Map<ServiceType, LinkChecker> linkCheckerDict() {
            var linkCheckerDict = new HashMap<ServiceType, LinkChecker>();

            linkCheckerDict.put(ServiceType.GitHub, mock(GitHubLinkChecker.class));
            linkCheckerDict.put(ServiceType.StackOverflow, mock(StackOverflowLinkChecker.class));

            return linkCheckerDict;
        }

        @Bean
        public JdbcLinkService jdbcLinkService(
            JdbcChatDao chatDao,
            JdbcLinkDao linkDao,
            JdbcSubscriptionDao subscriptionDao,
            LinkParser linkParser,
            Map<ServiceType, LinkChecker> linkCheckerDict
        ) {
            return new JdbcLinkService(
                chatDao,
                linkDao,
                subscriptionDao,
                linkParser,
                linkCheckerDict
            );
        }
    }
    @Autowired
    private JdbcChatDao chatDao;
    @Autowired
    private JdbcLinkDao linkDao;
    @Autowired
    private JdbcSubscriptionDao subscriptionDao;
    @Autowired
    private LinkParser linkParser;
    @Autowired
    private Map<ServiceType, LinkChecker> linkCheckerDict;
    private JdbcLinkService jdbcLinkService;

    @BeforeEach
    public void beforeEach() {
        jdbcLinkService = new JdbcLinkService(
            chatDao,
            linkDao,
            subscriptionDao,
            linkParser,
            linkCheckerDict
        );
    }

    @Test
    @DisplayName("Проверим, что мы возвращаем ссылки")
    public void testGetByChatId_whenChatIdExists_shouldReturnLinks() throws EntityNotFoundException {
        long chatId = 1L;
        SubscriptionDto subscriptionDto = new SubscriptionDto(1L, chatId, 1L, "alias");
        LinkDto linkDto = new LinkDto(1L, URI.create("http://example.com"), "SERVICE", "{}", null);
        when(chatDao.findById(chatId)).thenReturn(java.util.Optional.of(new ChatDto(chatId)));
        when(subscriptionDao.findByChatId(chatId)).thenReturn(List.of(subscriptionDto));
        when(linkDao.findById(subscriptionDto.linkId())).thenReturn(java.util.Optional.of(linkDto));

        List<Link> result = jdbcLinkService.getByChatId(chatId);

        assertThat(result).hasSize(1);
        assertThat(result.getFirst().id()).isEqualTo(subscriptionDto.id());
        assertThat(result.getFirst().link()).isEqualTo(linkDto.url());
        assertThat(result.getFirst().alias()).isEqualTo(subscriptionDto.alias());
    }

    @Test
    @DisplayName("Проверим, что мы правильно добавляем ссылку")
    public void testTrackLink_whenChatIdExistsAndLinkIsValid_shouldAddLink() throws EntityNotFoundException {
        long chatId = 1L;
        URI url = URI.create("http://example.com");
        String alias = "alias";
        AddLinkRequest addLinkRequest = new AddLinkRequest(url, alias);
        when(chatDao.findById(chatId)).thenReturn(java.util.Optional.of(new ChatDto(chatId)));
        when(subscriptionDao.findByChatIdAndAlias(chatId, alias)).thenReturn(java.util.Optional.empty());
        when(linkDao.findByUrl(url)).thenReturn(java.util.Optional.empty());
        when(linkParser.parse(url)).thenReturn(new LinkDescriptor(ServiceType.GitHub, Map.of()));
        when(linkCheckerDict.get(ServiceType.GitHub).check(any())).thenReturn(Map.of());
        when(linkDao.add(any(LinkDto.class))).thenReturn(new LinkDto(1L, url, "GitHub", "{}", null));
        when(subscriptionDao.add(any(SubscriptionDto.class))).thenReturn(new SubscriptionDto(1L, chatId, 1L, alias));

        Link result = jdbcLinkService.trackLink(chatId, addLinkRequest);

        assertThat(result.id()).isEqualTo(1L);
        assertThat(result.link()).isEqualTo(url);
        assertThat(result.alias()).isEqualTo(alias);
    }

    @Test
    @DisplayName("Проверим, что мы правильно удаляем ссылку")
    public void testUntrackLink_whenChatIdExistsAndAliasIsValid_shouldRemoveLink() throws EntityNotFoundException {
        long chatId = 1L;
        String alias = "alias";
        RemoveLinkRequest removeLinkRequest = new RemoveLinkRequest(alias);
        SubscriptionDto subscriptionDto = new SubscriptionDto(1L, chatId, 1L, alias);
        LinkDto linkDto = new LinkDto(1L, URI.create("http://example.com"), "SERVICE", "{}", null);
        when(chatDao.findById(chatId)).thenReturn(java.util.Optional.of(new ChatDto(chatId)));
        when(subscriptionDao.findByChatIdAndAlias(chatId, alias)).thenReturn(java.util.Optional.of(subscriptionDto));
        when(linkDao.findById(subscriptionDto.linkId())).thenReturn(java.util.Optional.of(linkDto));
        when(subscriptionDao.findByLinkId(linkDto.id())).thenReturn(List.of());

        Link result = jdbcLinkService.untrackLink(chatId, removeLinkRequest);

        assertThat(result.id()).isEqualTo(subscriptionDto.id());
        assertThat(result.link()).isEqualTo(linkDto.url());
        assertThat(result.alias()).isEqualTo(subscriptionDto.alias());
        verify(subscriptionDao).remove(subscriptionDto.id());
        verify(linkDao).remove(linkDto.id());
    }

    @Test
    @DisplayName("Проверим, что мы отлавливаем несуществующий чат")
    public void testGetByChatId_whenChatIdDoesNotExist_shouldThrowEntityNotFoundException() {
        long chatId = 1L;
        when(chatDao.findById(chatId)).thenReturn(java.util.Optional.empty());

        assertThatThrownBy(() -> jdbcLinkService.getByChatId(chatId))
            .isInstanceOf(EntityNotFoundException.class)
            .hasMessageContaining("Chat with id %d not exist".formatted(chatId));
    }

    @Test
    @DisplayName("Проверим, что мы проверяем на дубликат alias")
    public void testTrackLink_whenAliasAlreadyExists_shouldThrowEntityAlreadyExistException() {
        long chatId = 1L;
        URI url = URI.create("http://example.com");
        String alias = "alias";
        AddLinkRequest addLinkRequest = new AddLinkRequest(url, alias);
        when(chatDao.findById(chatId)).thenReturn(java.util.Optional.of(new ChatDto(chatId)));
        when(subscriptionDao.findByChatIdAndAlias(chatId, alias)).thenReturn(java.util.Optional.of(new SubscriptionDto(1L, chatId , 1L, alias)));

        assertThatThrownBy(() -> jdbcLinkService.trackLink(chatId, addLinkRequest))
            .isInstanceOf(EntityAlreadyExistException.class)
            .hasMessageContaining("Alias is already in use by you: " + alias);
    }

    @Test
    @DisplayName("Проверим, что мы отлавливаем несуществующий alias")
    public void testUntrackLink_whenAliasDoesNotExist_shouldThrowEntityNotFoundException() {
        long chatId = 1L;
        String alias = "alias";
        RemoveLinkRequest removeLinkRequest = new RemoveLinkRequest(alias);
        when(chatDao.findById(chatId)).thenReturn(java.util.Optional.of(new ChatDto(chatId)));
        when(subscriptionDao.findByChatIdAndAlias(chatId, alias)).thenReturn(java.util.Optional.empty());

        assertThatThrownBy(() -> jdbcLinkService.untrackLink(chatId, removeLinkRequest))
            .isInstanceOf(EntityNotFoundException.class)
            .hasMessageContaining("Link not found for alias: " + alias);
    }

    @Test
    @DisplayName("Проверим, что мы отлавливаем несуществующий чат при добавлении ссылки")
    public void testTrackLink_whenChatIdDoesNotExist_shouldThrowEntityNotFoundException() {
        long chatId = 1L;
        URI url = URI.create("http://example.com");
        AddLinkRequest addLinkRequest = new AddLinkRequest(url, null);
        when(chatDao.findById(chatId)).thenReturn(java.util.Optional.empty());

        assertThatThrownBy(() -> jdbcLinkService.trackLink(chatId, addLinkRequest))
            .isInstanceOf(EntityNotFoundException.class)
            .hasMessageContaining("Chat with id %d not exist".formatted(chatId));
    }

    @Test
    @DisplayName("Проверим, что мы отлавливаем несуществующий чат при удалении ссылки")
    public void testUntrackLink_whenChatIdDoesNotExist_shouldThrowEntityNotFoundException() {
        long chatId = 1L;
        RemoveLinkRequest removeLinkRequest = new RemoveLinkRequest("alias");
        when(chatDao.findById(chatId)).thenReturn(java.util.Optional.empty());

        assertThatThrownBy(() -> jdbcLinkService.untrackLink(chatId, removeLinkRequest))
            .isInstanceOf(EntityNotFoundException.class)
            .hasMessageContaining("Chat with id %d not exist".formatted(chatId));
    }

    @Test
    @DisplayName("Проверим, что мы правильно обновляем ссылки")
    public void testUpdateLinksFrom_whenLinksExistAndHaveUpdates_shouldReturnLinkUpdates() {
        OffsetDateTime from = OffsetDateTime.now().minusDays(1);
        LinkDto linkDto = new LinkDto(1L, URI.create("http://example.com"), "GitHub", "{}", null);
        when(linkDao.findFromLastUpdate(from)).thenReturn(List.of(linkDto));
        when(linkCheckerDict.get(ServiceType.GitHub).check(any())).thenReturn(Map.of("key", "value"));
        when(linkCheckerDict.get(ServiceType.GitHub).toUpdateMessage(any())).thenReturn("В репозитории произошло обновление");
        when(subscriptionDao.findByLinkId(linkDto.id())).thenReturn(List.of(new SubscriptionDto(1L, 1L, 1L, "alias")));

        List<LinkUpdateDto> result = jdbcLinkService.updateLinksFrom(from);

        assertThat(result).hasSize(1);
        assertThat(result.getFirst().id()).isEqualTo(linkDto.id());
        assertThat(result.getFirst().link()).isEqualTo(linkDto.url());
        assertThat(result.getFirst().description()).contains("В репозитории произошло обновление");
        assertThat(result.getFirst().tgChatIds()).contains(1L);
    }

    @Test
    @DisplayName("Проверим, что мы не возвращаем обновления для ссылок без изменений")
    public void testUpdateLinksFrom_whenLinksExistButNoUpdates_shouldReturnEmptyList() {
        OffsetDateTime from = OffsetDateTime.now().minusDays(1);
        LinkDto linkDto = new LinkDto(1L, URI.create("http://example.com"), "GitHub", "{}", null);
        when(linkDao.findFromLastUpdate(from)).thenReturn(List.of(linkDto));
        when(linkCheckerDict.get(ServiceType.GitHub).check(any())).thenReturn(Map.of());

        List<LinkUpdateDto> result = jdbcLinkService.updateLinksFrom(from);

        assertThat(result).isEmpty();
    }
}
