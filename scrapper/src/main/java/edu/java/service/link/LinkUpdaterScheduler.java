package edu.java.service.link;

import edu.java.client.bot.BotClient;
import edu.java.client.exception.ClientException;
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

    @Scheduled(fixedDelayString = "#{@'app-edu.java.configuration.ApplicationConfig'.scheduler.interval}")
    public void update() {
        LOGGER.info("fetching updates...");

        OffsetDateTime from = OffsetDateTime.now().minusSeconds(FROM_SECONDS);

        var updates = linkService.updateLinksFrom(from);

        updates.forEach(linkUpdate -> {
            try {
                botClient.sendLinkUpdate(linkUpdate);
            } catch (ClientException e) {
                LOGGER.error(e);
            }
        });
    }
}
