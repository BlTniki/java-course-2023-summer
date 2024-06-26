package edu.java.domain.link.service.updater;

import edu.java.client.bot.BotClient;
import edu.java.client.bot.model.LinkUpdate;
import edu.java.client.exception.ClientException;
import edu.java.domain.link.service.LinkService;
import java.time.OffsetDateTime;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.Executor;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

public class HttpLinkUpdater implements LinkUpdater {
    private static final Logger LOGGER = LogManager.getLogger();
    private final LinkService linkService;
    private final BotClient botClient;
    private final Executor executor;

    public HttpLinkUpdater(LinkService linkService, BotClient botClient, Executor executor) {
        this.linkService = linkService;
        this.botClient = botClient;
        this.executor = executor;
    }

    @Override
    public void checkUpdatesAndNotify(OffsetDateTime from) {
        var updates = linkService.updateLinksFrom(from);

        updates.stream()
            .map(linkUpdate -> CompletableFuture.runAsync(() -> {
                try {
                    LOGGER.info("Notify about updates from: " + linkUpdate.link());
                    botClient.sendLinkUpdate(LinkUpdate.fromServiceModel(linkUpdate));
                } catch (ClientException e) {
                    LOGGER.error(e);
                }
            }, executor))
            .forEach(CompletableFuture::join);
    }
}
