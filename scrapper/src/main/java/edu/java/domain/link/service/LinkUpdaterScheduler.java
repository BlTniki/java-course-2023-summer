package edu.java.domain.link.service;

import edu.java.client.bot.BotClient;
import edu.java.client.bot.model.LinkUpdate;
import edu.java.client.exception.ClientException;
import edu.java.domain.link.dto.LinkUpdateDto;
import java.time.OffsetDateTime;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.springframework.scheduling.annotation.Scheduled;

public class LinkUpdaterScheduler {
    private static final Logger LOGGER = LogManager.getLogger();
    private static final int FROM_SECONDS = 10;
    private final LinkService linkService;
    private final BotClient botClient;

    public LinkUpdaterScheduler(LinkService linkService, BotClient botClient) {
        this.linkService = linkService;
        this.botClient = botClient;
    }

    private LinkUpdate mapToClientUpdate(LinkUpdateDto serviceUpdate) {
        return new LinkUpdate(
            serviceUpdate.id(),
            serviceUpdate.link(),
            serviceUpdate.description(),
            serviceUpdate.tgChatIds()
        );
    }

    @Scheduled(fixedDelayString = "#{@'app-edu.java.configuration.ApplicationConfig'.scheduler.interval}")
    public void update() {
        LOGGER.info("fetching updates...");

        OffsetDateTime from = OffsetDateTime.now().minusSeconds(FROM_SECONDS);

        var updates = linkService.updateLinksFrom(from);

        updates.forEach(linkUpdate -> {
            try {
                LOGGER.info("New update: " + linkUpdate.link());
                botClient.sendLinkUpdate(mapToClientUpdate(linkUpdate));
            } catch (ClientException e) {
                LOGGER.error(e);
            }
        });
    }
}
