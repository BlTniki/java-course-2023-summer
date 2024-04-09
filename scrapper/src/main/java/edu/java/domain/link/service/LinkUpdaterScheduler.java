package edu.java.domain.link.service;

import edu.java.domain.link.service.updater.LinkUpdater;
import java.time.OffsetDateTime;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.springframework.scheduling.annotation.Scheduled;

public class LinkUpdaterScheduler {
    private static final Logger LOGGER = LogManager.getLogger();
    private static final int FROM_SECONDS = 10;
    private final LinkUpdater linkUpdater;

    public LinkUpdaterScheduler(LinkUpdater linkUpdater) {
        this.linkUpdater = linkUpdater;
    }

    @Scheduled(fixedDelayString = "#{@'app-edu.java.configuration.ApplicationConfig'.scheduler.interval}")
    public void update() {
        LOGGER.info("fetching updates...");

        OffsetDateTime from = OffsetDateTime.now().minusSeconds(FROM_SECONDS);

        linkUpdater.checkUpdatesAndNotify(from);
    }
}
