package edu.java.bot.controller.listener;

import edu.java.bot.controller.model.LinkUpdate;
import edu.java.bot.service.UpdatesService;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.kafka.support.Acknowledgment;
import org.springframework.messaging.handler.annotation.Payload;

/**
 * Этот класс отвечает за получение оповещений об обновлении ресурсов
 * через Kafka
 */
public class LinkUpdatesListener {
    private static final Logger LOGGER = LogManager.getLogger();
    private final UpdatesService updatesService;
    private final KafkaTemplate<String, LinkUpdate> dlqLinkUpdateProducer;
    private final String dlqTopic;

    public LinkUpdatesListener(
        UpdatesService updatesService,
        KafkaTemplate<String, LinkUpdate> dlqLinkUpdateProducer,
        String dlqTopic
    ) {
        this.updatesService = updatesService;
        this.dlqLinkUpdateProducer = dlqLinkUpdateProducer;
        this.dlqTopic = dlqTopic;
    }

    @KafkaListener(
        topics = "#{@'kafka-edu.java.configuration.KafkaConfiguration'.consumer().topic().name()}",
        containerFactory = "linkUpdateContainerFactory"
    )
    public void handleMessage(@Payload LinkUpdate linkUpdate, Acknowledgment acknowledgment) {
        LOGGER.info("Received from kafka link update with id: " + linkUpdate.id());

        try {
            updatesService.processLinkUpdate(linkUpdate);
        } catch (Exception e) {
            LOGGER.error("Failed process link update with id: " + linkUpdate.id(), e);
            dlqLinkUpdateProducer.send(dlqTopic, String.valueOf(linkUpdate.id()), linkUpdate)
                .exceptionally(ex -> {
                    LOGGER.error("Failed send to kafka this dlq link update: " + linkUpdate, ex);
                    return null;
                });
        }

        acknowledgment.acknowledge();
    }
}
