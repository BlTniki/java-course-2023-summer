package edu.java.service.link;

import edu.java.ScrapperApplicationTests;
import edu.java.controller.model.AddLinkRequest;
import edu.java.controller.model.RemoveLinkRequest;
import edu.java.domain.dao.chat.JdbcChatDao;
import edu.java.domain.dao.link.JdbcLinkDao;
import edu.java.domain.dao.subscription.JdbcSubscriptionDao;
import edu.java.domain.dto.ChatDto;
import edu.java.domain.dto.LinkDto;
import edu.java.domain.dto.SubscriptionDto;
import edu.java.service.exception.EntityAlreadyExistException;
import edu.java.service.exception.EntityNotFoundException;
import edu.java.service.link.model.Link;
import edu.java.service.link.model.LinkDescriptor;
import edu.java.service.link.model.ServiceType;
import java.net.URI;
import java.util.List;
import java.util.Map;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

class JdbcLinkServiceTest extends ScrapperApplicationTests {
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
    @Autowired
    private JdbcLinkService jdbcLinkService;

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
}
