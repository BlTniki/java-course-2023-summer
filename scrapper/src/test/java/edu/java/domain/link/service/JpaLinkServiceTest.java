package edu.java.domain.link.service;

import edu.java.ScrapperApplicationTests;
import edu.java.controller.model.AddLinkRequest;
import edu.java.controller.model.ErrorCode;
import edu.java.controller.model.RemoveLinkRequest;
import edu.java.domain.chat.dao.JpaChatDao;
import edu.java.domain.chat.dto.JpaChatEntity;
import edu.java.domain.exception.EntityAlreadyExistException;
import edu.java.domain.exception.EntityNotFoundException;
import edu.java.domain.link.dao.JpaLinkDao;
import edu.java.domain.link.dao.JpaSubscriptionDao;
import edu.java.domain.link.dto.JpaLinkEntity;
import edu.java.domain.link.dto.JpaSubscriptionEntity;
import edu.java.domain.link.dto.Link;
import edu.java.domain.link.dto.LinkDescriptor;
import edu.java.domain.link.dto.ServiceType;
import java.net.URI;
import java.time.OffsetDateTime;
import java.util.Map;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.TestConfiguration;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.context.annotation.Bean;
import org.springframework.test.annotation.Rollback;
import org.springframework.transaction.annotation.Transactional;
import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.when;

@Transactional
class JpaLinkServiceTest extends ScrapperApplicationTests {
    @TestConfiguration
    static class Config {
        @MockBean
        public LinkParser linkParser;
        @MockBean
        public Map<ServiceType, LinkChecker> linkCheckerDict;
        @Bean
        public JpaLinkService linkService(
            JpaChatDao chatDao,
            JpaLinkDao linkDao,
            JpaSubscriptionDao subscriptionDao,
            LinkParser linkParser,
            Map<ServiceType, LinkChecker> linkCheckerDict
        ) {
            return new JpaLinkService(
                chatDao,
                linkDao,
                subscriptionDao,
                linkParser,
                linkCheckerDict
            );
        }
    }

    @Autowired
    private JpaChatDao chatDao;
    @Autowired
    private JpaLinkDao linkDao;
    @Autowired
    private JpaSubscriptionDao subscriptionDao;
    @Autowired
    private LinkParser linkParser;
    @Autowired
    private Map<ServiceType, LinkChecker> linkCheckerDict;
    @MockBean
    private LinkChecker linkChecker;
    @Autowired
    private JpaLinkService linkService;

    private final long chatId = 1L;
    private final URI testUri = URI.create("http://test.com");
    private final String testAlias = "testAlias";
    private JpaChatEntity testChat;
    private JpaLinkEntity testLink;


    @Test
    @Rollback
    void testGetByChatId_whenChatExists_shouldReturnLinks() {
        // Arrange
        testChat = new JpaChatEntity(chatId);
        testChat = chatDao.save(testChat);

        testLink = new JpaLinkEntity(null, testUri, ServiceType.GitHub, Map.of(), OffsetDateTime.now());
        testLink = linkDao.save(testLink);

        JpaSubscriptionEntity subscription = new JpaSubscriptionEntity(null, testChat, testLink, testAlias);
        subscriptionDao.save(subscription);

        // Act
        var links = linkService.getByChatId(chatId);

        // Assert
        assertThat(links).hasSize(1);
        assertThat(links.getFirst().alias()).isEqualTo(testAlias);
        assertThat(links.getFirst().link()).isEqualTo(testUri);
    }

    @Test
    @Rollback
    void testGetByChatId_whenChatDoesNotExist_shouldThrowException() {
        // Act & Assert
        assertThatThrownBy(() -> linkService.getByChatId(999L))
            .isInstanceOf(EntityNotFoundException.class)
            .hasMessageContaining("Chat with id 999 not exist")
            .hasFieldOrPropertyWithValue("errorCode", ErrorCode.TG_CHAT_NOT_FOUND);
    }

    @Test
    @Rollback
    void testTrackLink_whenChatExistsAndLinkNotExist_shouldCreateAndAddLink() {
        // Arrange
        testChat = new JpaChatEntity(chatId);
        testChat = chatDao.save(testChat);

        when(linkParser.parse(any(URI.class))).thenReturn(new LinkDescriptor(ServiceType.GitHub, Map.of()));
        when(linkCheckerDict.get(ServiceType.GitHub)).thenReturn(linkChecker);
        when(linkChecker.check(any())).thenReturn(Map.of());

        AddLinkRequest request = new AddLinkRequest(testUri, null);

        // Act
        Link trackedLink = linkService.trackLink(chatId, request);

        // Assert
        assertThat(trackedLink.alias()).isNotNull();
        assertThat(trackedLink.link()).isEqualTo(testUri);
        assertThat(subscriptionDao.count()).isEqualTo(1);
    }

    @Test
    @Rollback
    void testTrackLink_whenChatExistsAndLinkNotTracked_shouldAddLink() {
        // Arrange
        testChat = new JpaChatEntity(chatId);
        testChat = chatDao.save(testChat);

        testLink = new JpaLinkEntity(null, testUri, ServiceType.GitHub, Map.of(), OffsetDateTime.now());
        testLink = linkDao.save(testLink);
        when(linkParser.parse(any(URI.class))).thenReturn(new LinkDescriptor(ServiceType.GitHub, Map.of()));
        when(linkCheckerDict.get(ServiceType.GitHub)).thenReturn(linkChecker);
        when(linkChecker.check(any())).thenReturn(Map.of());

        AddLinkRequest request = new AddLinkRequest(testUri, null);

        // Act
        Link trackedLink = linkService.trackLink(chatId, request);

        // Assert
        assertThat(trackedLink.alias()).isNotNull();
        assertThat(trackedLink.link()).isEqualTo(testUri);
        assertThat(subscriptionDao.count()).isEqualTo(1);
    }

    @Test
    @Rollback
    void testTrackLink_whenChatExistsAndLinkAlreadyTracked_shouldThrowException() {
        // Arrange
        testChat = new JpaChatEntity(chatId);
        testChat = chatDao.save(testChat);

        testLink = new JpaLinkEntity(null, testUri, ServiceType.GitHub, Map.of(), OffsetDateTime.now());
        testLink = linkDao.save(testLink);

        when(linkParser.parse(any(URI.class))).thenReturn(new LinkDescriptor(ServiceType.GitHub, Map.of()));

        AddLinkRequest request = new AddLinkRequest(testUri, testAlias);
        linkService.trackLink(chatId, request);

        // Act & Assert
        assertThatThrownBy(() -> linkService.trackLink(chatId, request))
            .isInstanceOf(EntityAlreadyExistException.class)
            .hasMessageContaining("Alias is already in use by you: " + testAlias)
            .hasFieldOrPropertyWithValue("errorCode", ErrorCode.ALIAS_ALREADY_EXIST);
    }

    @Test
    @Rollback
    void testUntrackLink_whenChatExistsAndLinkTracked_shouldRemoveLink() {
        // Arrange
        testChat = new JpaChatEntity(chatId);
        testChat = chatDao.save(testChat);

        testLink = new JpaLinkEntity(null, testUri, ServiceType.GitHub, Map.of(), OffsetDateTime.now());
        testLink = linkDao.save(testLink);

        AddLinkRequest addRequest = new AddLinkRequest(testUri, testAlias);
        linkService.trackLink(chatId, addRequest);
        RemoveLinkRequest removeRequest = new RemoveLinkRequest(testAlias);

        // Act
        Link untrackedLink = linkService.untrackLink(chatId, removeRequest);

        // Assert
        assertThat(untrackedLink.alias()).isEqualTo(testAlias);
        assertThat(untrackedLink.link()).isEqualTo(testUri);
        assertThat(subscriptionDao.count()).isZero();
    }

    @Test
    @Rollback
    void testUntrackLink_whenChatExistsAndLinkNotTracked_shouldThrowException() {
        // Arrange
        testChat = new JpaChatEntity(chatId);
        testChat = chatDao.save(testChat);

        RemoveLinkRequest removeRequest = new RemoveLinkRequest(testAlias);

        // Act & Assert
        assertThatThrownBy(() -> linkService.untrackLink(chatId, removeRequest))
            .isInstanceOf(EntityNotFoundException.class)
            .hasMessageContaining("Link not found for alias: " + testAlias)
            .hasFieldOrPropertyWithValue("errorCode", ErrorCode.ALIAS_NOT_FOUND);
    }

    @Test
    @Rollback
    void testUpdateLinksFrom_whenLinksNeedUpdate_shouldReturnUpdatedLinks() {
        // Arrange
        testChat = new JpaChatEntity(chatId);
        testChat = chatDao.save(testChat);

        OffsetDateTime from = OffsetDateTime.now();
        testLink = new JpaLinkEntity(null, testUri, ServiceType.GitHub, Map.of(), from.minusDays(1));
        testLink = linkDao.save(testLink);

        JpaSubscriptionEntity subscription = new JpaSubscriptionEntity(null, testChat, testLink, testAlias);
        subscriptionDao.save(subscription);

        when(linkCheckerDict.get(ServiceType.GitHub)).thenReturn(linkChecker);
        when(linkChecker.check(any())).thenReturn(Map.of("key", "value"));

        // Act
        var linkUpdates = linkService.updateLinksFrom(from);

        // Assert
        assertThat(linkUpdates).isNotEmpty();
        assertThat(linkUpdates.getFirst().tgChatIds()).contains(chatId);
    }

    @Test
    @Rollback
    void testUpdateLinksFrom_whenNoLinksNeedUpdate_shouldReturnEmptyList() {
        // Arrange
        testChat = new JpaChatEntity(chatId);
        testChat = chatDao.save(testChat);

        testLink = new JpaLinkEntity(null, testUri, ServiceType.GitHub, Map.of(), OffsetDateTime.now());
        testLink = linkDao.save(testLink);

        OffsetDateTime from = OffsetDateTime.now().plusDays(1);
        when(linkParser.parse(any(URI.class))).thenReturn(new LinkDescriptor(ServiceType.GitHub, Map.of()));
        when(linkCheckerDict.get(ServiceType.GitHub)).thenReturn(linkChecker);
        when(linkChecker.check(any())).thenReturn(Map.of());

        // Act
        var linkUpdates = linkService.updateLinksFrom(from);

        // Assert
        assertThat(linkUpdates).isEmpty();
    }
}
