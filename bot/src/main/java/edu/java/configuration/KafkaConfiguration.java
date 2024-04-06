package edu.java.configuration;

import edu.java.bot.controller.model.LinkUpdate;
import jakarta.validation.constraints.NotNull;
import java.util.HashMap;
import java.util.Map;
import org.apache.kafka.clients.admin.AdminClientConfig;
import org.apache.kafka.clients.admin.NewTopic;
import org.apache.kafka.clients.consumer.ConsumerConfig;
import org.apache.kafka.common.serialization.StringDeserializer;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.context.annotation.Bean;
import org.springframework.kafka.annotation.EnableKafka;
import org.springframework.kafka.config.ConcurrentKafkaListenerContainerFactory;
import org.springframework.kafka.config.TopicBuilder;
import org.springframework.kafka.core.ConsumerFactory;
import org.springframework.kafka.core.DefaultKafkaConsumerFactory;
import org.springframework.kafka.core.KafkaAdmin;
import org.springframework.kafka.listener.ContainerProperties;
import org.springframework.kafka.listener.DefaultErrorHandler;
import org.springframework.kafka.support.serializer.JsonDeserializer;
import org.springframework.util.backoff.BackOff;
import org.springframework.util.backoff.FixedBackOff;
import org.springframework.validation.annotation.Validated;

@Validated
@ConditionalOnProperty(prefix = "kafka", name = "enable", havingValue = "true")
@EnableKafka
@ConfigurationProperties(prefix = "kafka")
public record KafkaConfiguration(
    @NotNull Consumer consumer
) {
    @Bean
    public KafkaAdmin admin() {
        Map<String, Object> configs = new HashMap<>();
        configs.put(AdminClientConfig.BOOTSTRAP_SERVERS_CONFIG, consumer.bootstrapServers);
        return new KafkaAdmin(configs);
    }

    @Bean
    public NewTopic linkUpdatesTopic() {
        return TopicBuilder
            .name(consumer.topic.name)
            .partitions(consumer.topic.partitions)
            .replicas(consumer.topic.replicas)
            .compact()
            .build();
    }

    @Bean
    public ConsumerFactory<String, LinkUpdate> linkUpdateConsumerFactory() {
        Map<String, Object> props = new HashMap<>();
        props.put(ConsumerConfig.BOOTSTRAP_SERVERS_CONFIG, consumer.bootstrapServers());
        props.put(ConsumerConfig.GROUP_ID_CONFIG, consumer.groupId());
        props.put(ConsumerConfig.AUTO_OFFSET_RESET_CONFIG, consumer.autoOffsetReset());
        props.put(ConsumerConfig.MAX_POLL_INTERVAL_MS_CONFIG, consumer.maxPollIntervalMs());
        props.put(ConsumerConfig.ENABLE_AUTO_COMMIT_CONFIG, consumer.enableAutoCommit());
        props.put(ConsumerConfig.KEY_DESERIALIZER_CLASS_CONFIG, StringDeserializer.class);
        props.put(ConsumerConfig.VALUE_DESERIALIZER_CLASS_CONFIG, JsonDeserializer.class);
        props.put(JsonDeserializer.TRUSTED_PACKAGES, "*");
        props.put(JsonDeserializer.USE_TYPE_INFO_HEADERS, false);
        props.put(JsonDeserializer.VALUE_DEFAULT_TYPE, LinkUpdate.class);
        return new DefaultKafkaConsumerFactory<>(props);
    }

    @Bean
    public ConcurrentKafkaListenerContainerFactory<String, LinkUpdate> listenerContainerFactory(
            ConsumerFactory<String, LinkUpdate> consumerFactory
    ) {
        ConcurrentKafkaListenerContainerFactory<String, LinkUpdate> factory =
            new ConcurrentKafkaListenerContainerFactory<>();
        factory.setConsumerFactory(consumerFactory);
        factory.setConcurrency(consumer.concurrency);
        factory.getContainerProperties().setAckMode(ContainerProperties.AckMode.MANUAL);
        return factory;
    }

    @SuppressWarnings("checkstyle:MagicNumber")
    @Bean
    public DefaultErrorHandler errorHandler() {
        Logger logger = LogManager.getLogger();
        BackOff fixedBackOff = new FixedBackOff(5000, 3);
        DefaultErrorHandler errorHandler = new DefaultErrorHandler((consumerRecord, exception) ->
            logger.error(
                "Couldn't process message: {}; {}", consumerRecord.value().toString(), exception.toString()
            ),
            fixedBackOff
        );

        errorHandler.addNotRetryableExceptions(NullPointerException.class);
        return errorHandler;
    }

    public record Consumer(
        String bootstrapServers,
        String groupId,
        String autoOffsetReset,
        Integer maxPollIntervalMs,
        Boolean enableAutoCommit,
        Integer concurrency,
        Topic topic
    ) {}

    public record Topic(
        String name,
        Integer partitions,
        Integer replicas
    ) {}
}
