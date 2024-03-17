package edu.java.service.link;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.springframework.scheduling.annotation.Scheduled;

public class LinkUpdaterScheduler {
    private static final Logger LOGGER = LogManager.getLogger();

    @Scheduled(fixedDelayString = "#{@'app-edu.java.configuration.ApplicationConfig'.scheduler.interval}")
    public void update() {
        LOGGER.info("wololo");
    }
}
