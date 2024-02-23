package edu.java;

import org.apache.logging.log4j.Logger;
import org.springframework.scheduling.annotation.Scheduled;

public class LinkUpdaterScheduler {
    private final Logger logger;

    public LinkUpdaterScheduler(Logger logger) {
        this.logger = logger;
    }

    @Scheduled(fixedDelayString = "#{@'app-edu.java.configuration.ApplicationConfig'.scheduler.interval}")
    public void update() {
        logger.info("wololo");
    }
}
