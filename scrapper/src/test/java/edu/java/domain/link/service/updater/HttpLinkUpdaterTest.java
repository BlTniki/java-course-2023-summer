package edu.java.domain.link.service.updater;

import edu.java.ScrapperApplicationTests;
import edu.java.client.bot.BotClient;
import edu.java.client.bot.model.LinkUpdate;
import edu.java.client.exception.ClientException;
import edu.java.domain.link.dto.LinkUpdateDto;
import edu.java.domain.link.service.LinkService;
import java.net.URI;
import java.time.OffsetDateTime;
import java.util.List;
import java.util.concurrent.Executor;
import java.util.concurrent.Executors;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.TestConfiguration;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.context.annotation.Bean;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

class HttpLinkUpdaterTest extends ScrapperApplicationTests {
    @TestConfiguration
    public static class Config {
        @Bean
        public Executor executor() {
            return Executors.newVirtualThreadPerTaskExecutor();
        }
        @Bean
        public HttpLinkUpdater httpLinkUpdater(
            LinkService linkService,
            BotClient botClient,
            Executor executor
        ) {
            return new HttpLinkUpdater(linkService, botClient, executor);
        }
    }

    @MockBean
    public LinkService linkService;
    @MockBean
    public BotClient botClient;
    @Autowired
    public HttpLinkUpdater httpLinkUpdater;

    @Test
    @DisplayName("Проверим, что мы передаём сообщение клиенту")
    void checkUpdatesAndNotify() throws ClientException {
        var entity = new LinkUpdateDto(1, URI.create("wololo"), "kek", List.of(1L));
        when(linkService.updateLinksFrom(any())).thenReturn(List.of(entity));

        httpLinkUpdater.checkUpdatesAndNotify(OffsetDateTime.now());

        verify(botClient).sendLinkUpdate(LinkUpdate.fromServiceModel(entity));
    }
}
