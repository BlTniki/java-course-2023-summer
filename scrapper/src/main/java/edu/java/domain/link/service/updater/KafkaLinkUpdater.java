package edu.java.domain.link.service.updater;

import edu.java.domain.link.dto.LinkUpdateDto;
import edu.java.domain.link.service.LinkService;
import java.time.OffsetDateTime;
import java.util.concurrent.CompletableFuture;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.springframework.kafka.core.KafkaTemplate;

public class KafkaLinkUpdater implements LinkUpdater {
    private static final Logger LOGGER = LogManager.getLogger();

    private final LinkService linkService;
    private final String topic;
    private final KafkaTemplate<String, LinkUpdateDto> linkUpdatesProducer;

    public KafkaLinkUpdater(
        LinkService linkService,
        String topic,
        KafkaTemplate<String, LinkUpdateDto> linkUpdatesProducer
    ) {
        this.linkService = linkService;
        this.topic = topic;
        this.linkUpdatesProducer = linkUpdatesProducer;
    }

    @Override
    public void checkUpdatesAndNotify(OffsetDateTime from) {
        var updates = linkService.updateLinksFrom(from);

        try {
            updates.stream()
                .map(update -> {
                    LOGGER.info("Notify about updates from: " + update.link());
                    return linkUpdatesProducer.send(topic, String.valueOf(update.id()), update);
                })
                .forEach(CompletableFuture::join);
        } catch (Exception e) {
            LOGGER.error("Error occurred during sending to Kafka", e);
        }
    }
}
