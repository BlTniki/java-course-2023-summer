package edu.java.bot.controller.listener;

import edu.java.BotApplicationTests;
import edu.java.bot.controller.model.LinkUpdate;
import edu.java.bot.service.UpdatesService;
import java.net.URI;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import org.apache.kafka.clients.admin.AdminClientConfig;
import org.apache.kafka.clients.admin.NewTopic;
import org.apache.kafka.clients.consumer.ConsumerConfig;
import org.apache.kafka.clients.producer.ProducerConfig;
import org.apache.kafka.common.serialization.StringDeserializer;
import org.apache.kafka.common.serialization.StringSerializer;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.test.context.TestConfiguration;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.context.annotation.Bean;
import org.springframework.kafka.config.ConcurrentKafkaListenerContainerFactory;
import org.springframework.kafka.config.TopicBuilder;
import org.springframework.kafka.core.ConsumerFactory;
import org.springframework.kafka.core.DefaultKafkaConsumerFactory;
import org.springframework.kafka.core.DefaultKafkaProducerFactory;
import org.springframework.kafka.core.KafkaAdmin;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.kafka.core.ProducerFactory;
import org.springframework.kafka.listener.ContainerProperties;
import org.springframework.kafka.support.serializer.JsonDeserializer;
import org.springframework.kafka.support.serializer.JsonSerializer;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.doThrow;
import static org.mockito.Mockito.spy;
import static org.mockito.Mockito.verify;

class LinkUpdatesListenerTest extends BotApplicationTests {
    @TestConfiguration
    public static class Config {
        @Value("${kafka.consumer.topic.name}")
        public String topic;

        @Bean
        public KafkaAdmin admin() {
            Map<String, Object> configs = new HashMap<>();
            configs.put(AdminClientConfig.BOOTSTRAP_SERVERS_CONFIG, KAFKA.getBootstrapServers());
            return new KafkaAdmin(configs);
        }

        @Bean
        public NewTopic topicDlq() {
            return TopicBuilder
                .name("test_dlq")
                .partitions(1)
                .replicas(1)
                .compact()
                .build();
        }

        @Bean
        public NewTopic topicUpdates() {
            return TopicBuilder
                .name(topic)
                .partitions(1)
                .replicas(1)
                .compact()
                .build();
        }

        @Bean
        public ProducerFactory<String, LinkUpdate> producerFactory() {
            Map<String, Object> configProps = new HashMap<>();
            configProps.put(ProducerConfig.BOOTSTRAP_SERVERS_CONFIG, KAFKA.getBootstrapServers());
            configProps.put(ProducerConfig.KEY_SERIALIZER_CLASS_CONFIG, StringSerializer.class);
            configProps.put(ProducerConfig.VALUE_SERIALIZER_CLASS_CONFIG, JsonSerializer.class);
            return new DefaultKafkaProducerFactory<>(configProps);
        }

        @Bean
        public KafkaTemplate<String, LinkUpdate> dlqLinkUpdateProducer() {
            return spy(new KafkaTemplate<>(producerFactory()));
        }

        @Bean
        public ConsumerFactory<String, LinkUpdate> linkUpdateConsumerFactory() {
            Map<String, Object> props = new HashMap<>();
            props.put(ConsumerConfig.BOOTSTRAP_SERVERS_CONFIG, KAFKA.getBootstrapServers());
            props.put(ConsumerConfig.GROUP_ID_CONFIG, "bot");
            props.put(ConsumerConfig.AUTO_OFFSET_RESET_CONFIG, "earliest");
            props.put(ConsumerConfig.KEY_DESERIALIZER_CLASS_CONFIG, StringDeserializer.class);
            props.put(ConsumerConfig.VALUE_DESERIALIZER_CLASS_CONFIG, JsonDeserializer.class);
            props.put(JsonDeserializer.VALUE_DEFAULT_TYPE, LinkUpdate.class);
            return new DefaultKafkaConsumerFactory<>(props);
        }

        @Bean
        public ConcurrentKafkaListenerContainerFactory<String, LinkUpdate> linkUpdateContainerFactory(
            ConsumerFactory<String, LinkUpdate> consumerFactory
        ) {
            ConcurrentKafkaListenerContainerFactory<String, LinkUpdate> factory =
                new ConcurrentKafkaListenerContainerFactory<>();
            factory.setConsumerFactory(consumerFactory);
            factory.setConcurrency(1);
            factory.getContainerProperties().setAckMode(ContainerProperties.AckMode.MANUAL);
            return factory;
        }

        @Bean
        public LinkUpdatesListener linkUpdatesListener(
            UpdatesService updatesService,
            KafkaTemplate<String, LinkUpdate> dlqLinkUpdateProducer
        ) {
            return new LinkUpdatesListener(updatesService, dlqLinkUpdateProducer, "test_dlq");
        }
    }

    @Value("${kafka.consumer.topic.name}")
    public String topic;

    @MockBean
    private UpdatesService updatesService;
    @Autowired
    private KafkaTemplate<String, LinkUpdate> dlqLinkUpdateProducer;
    @Autowired
    private LinkUpdatesListener linkUpdatesListener;

    @Test
    @DisplayName("Проверим, что мы получаем сообщение из кафки")
    void handleMessage() {
        var entity = new LinkUpdate(1, URI.create("wololo"), "kek", List.of(1L));
        // Используем dlq продюсер, но с другим топиком, просто, чтобы накинуть данных
        dlqLinkUpdateProducer.send(topic, String.valueOf(entity.id()), entity);

        try {
            Thread.sleep(1_000);
        } catch (InterruptedException e) {
            throw new RuntimeException(e);
        }

        verify(updatesService).processLinkUpdate(entity);
    }

    @Test
    @DisplayName("Проверим, что мы передаём плохое сообщение в dlq")
    void handleMessage_dlq() {
        var entity = new LinkUpdate(1, URI.create("wololo"), "kek", List.of(1L));
        // Используем dlq продюсер, но с другим топиком, просто, чтобы накинуть данных
        dlqLinkUpdateProducer.send(topic, String.valueOf(entity.id()), entity);
        doThrow(new RuntimeException()).when(updatesService).processLinkUpdate(any());

        try {
            Thread.sleep(1_000);
        } catch (InterruptedException e) {
            throw new RuntimeException(e);
        }

        verify(dlqLinkUpdateProducer).send("test_dlq", String.valueOf(entity.id()), entity);
    }
}
