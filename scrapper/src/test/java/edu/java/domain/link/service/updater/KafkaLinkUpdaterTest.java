package edu.java.domain.link.service.updater;

import edu.java.ScrapperApplicationTests;
import edu.java.domain.link.dto.LinkUpdateDto;
import edu.java.domain.link.service.LinkService;
import java.net.URI;
import java.time.OffsetDateTime;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import org.apache.kafka.clients.admin.AdminClientConfig;
import org.apache.kafka.clients.admin.NewTopic;
import org.apache.kafka.clients.producer.ProducerConfig;
import org.apache.kafka.common.serialization.StringSerializer;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.TestConfiguration;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.context.annotation.Bean;
import org.springframework.kafka.config.TopicBuilder;
import org.springframework.kafka.core.DefaultKafkaProducerFactory;
import org.springframework.kafka.core.KafkaAdmin;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.kafka.core.ProducerFactory;
import org.springframework.kafka.support.serializer.JsonSerializer;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.spy;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

class KafkaLinkUpdaterTest extends ScrapperApplicationTests {
    @TestConfiguration
    public static class Config {
        @Bean
        public KafkaAdmin admin() {
            Map<String, Object> configs = new HashMap<>();
            configs.put(AdminClientConfig.BOOTSTRAP_SERVERS_CONFIG, KAFKA.getBootstrapServers());
            return new KafkaAdmin(configs);
        }

        @Bean
        public NewTopic topic1() {
            return TopicBuilder
                .name("test_scrapper")
                .partitions(1)
                .replicas(1)
                .compact()
                .build();
        }

        @Bean
        public ProducerFactory<String, LinkUpdateDto> producerFactory() {
            Map<String, Object> configProps = new HashMap<>();
            configProps.put(ProducerConfig.BOOTSTRAP_SERVERS_CONFIG, KAFKA.getBootstrapServers());
            configProps.put(ProducerConfig.KEY_SERIALIZER_CLASS_CONFIG, StringSerializer.class);
            configProps.put(ProducerConfig.VALUE_SERIALIZER_CLASS_CONFIG, JsonSerializer.class);
            return new DefaultKafkaProducerFactory<>(configProps);
        }

        @Bean
        public KafkaTemplate<String, LinkUpdateDto> linkUpdateProducer() {
            return spy(new KafkaTemplate<>(producerFactory()));
        }

        @Bean
        public KafkaLinkUpdater kafkaLinkUpdater(
            LinkService linkService,
            KafkaTemplate<String, LinkUpdateDto> linkUpdateProducer
        ) {
            return new KafkaLinkUpdater(linkService, "test_scrapper", linkUpdateProducer);
        }
    }

    @MockBean
    public LinkService linkService;
    @Autowired
    public KafkaLinkUpdater linkUpdater;
    @Autowired
    public KafkaTemplate<String, LinkUpdateDto> linkUpdateProducer;

    @Test
    @DisplayName("Проверим, что мы передаём сообщение кафке и не падаем")
    void checkUpdatesAndNotify() {
        var entity = new LinkUpdateDto(1, URI.create("wololo"), "kek", List.of(1L));
        when(linkService.updateLinksFrom(any())).thenReturn(List.of(entity));

        linkUpdater.checkUpdatesAndNotify(OffsetDateTime.now());

        verify(linkUpdateProducer).send("test_scrapper", String.valueOf(entity.id()), entity);
    }
}
