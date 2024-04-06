package edu.java.configuration;

import edu.java.domain.link.dto.LinkUpdateDto;
import jakarta.validation.constraints.NotNull;
import java.time.Duration;
import java.util.HashMap;
import java.util.Map;
import org.apache.kafka.clients.admin.AdminClientConfig;
import org.apache.kafka.clients.admin.NewTopic;
import org.apache.kafka.clients.producer.ProducerConfig;
import org.apache.kafka.common.serialization.StringSerializer;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.context.annotation.Bean;
import org.springframework.kafka.config.TopicBuilder;
import org.springframework.kafka.core.DefaultKafkaProducerFactory;
import org.springframework.kafka.core.KafkaAdmin;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.kafka.core.ProducerFactory;
import org.springframework.kafka.support.serializer.JsonSerializer;
import org.springframework.validation.annotation.Validated;

@Validated
@ConditionalOnProperty(prefix = "app", name = "notification-type", havingValue = "kafka")
@ConfigurationProperties()
public record KafkaConfiguration(
    @NotNull Kafka kafka
) {
    @Bean
    public KafkaAdmin admin() {
        Map<String, Object> configs = new HashMap<>();
        configs.put(AdminClientConfig.BOOTSTRAP_SERVERS_CONFIG, kafka.bootstrapServers);
        return new KafkaAdmin(configs);
    }

    @Bean
    public NewTopic topic1() {
        return TopicBuilder
            .name(kafka.topic.name)
            .partitions(kafka.topic.partitions)
            .replicas(kafka.topic.replicas)
            .compact()
            .build();
    }

    @Bean
    public ProducerFactory<String, LinkUpdateDto> producerFactory() {
        Map<String, Object> props = new HashMap<>();
        props.put(ProducerConfig.BOOTSTRAP_SERVERS_CONFIG, kafka.bootstrapServers);
        props.put(ProducerConfig.CLIENT_ID_CONFIG, kafka.clientId);
        props.put(ProducerConfig.ACKS_CONFIG, kafka.askMode);
        props.put(ProducerConfig.DELIVERY_TIMEOUT_MS_CONFIG, (int) kafka.deliveryTimeout.toMillis());
        props.put(ProducerConfig.LINGER_MS_CONFIG, kafka.lingerMs);
        props.put(ProducerConfig.BATCH_SIZE_CONFIG, kafka.batchSize);
        props.put(ProducerConfig.MAX_IN_FLIGHT_REQUESTS_PER_CONNECTION, kafka.maxInFlightPerConnection);
        props.put(ProducerConfig.ENABLE_IDEMPOTENCE_CONFIG, kafka.enableIdempotence);
        props.put(ProducerConfig.KEY_SERIALIZER_CLASS_CONFIG, StringSerializer.class);
        props.put(ProducerConfig.VALUE_SERIALIZER_CLASS_CONFIG, JsonSerializer.class);
        return new DefaultKafkaProducerFactory<>(props);
    }

    @Bean
    public KafkaTemplate<String, LinkUpdateDto> linkUpdatesProducer(
            ProducerFactory<String, LinkUpdateDto> producerFactory
    ) {
        return new KafkaTemplate<>(producerFactory);
    }

    public record Kafka(
        String bootstrapServers,
        String clientId,
        String askMode,
        Duration deliveryTimeout,
        Integer lingerMs,
        Integer batchSize,
        Integer maxInFlightPerConnection,
        Boolean enableIdempotence,
        Topic topic
    ) {}

    public record Topic(
        String name,
        Integer partitions,
        Integer replicas
    ) {}
}
