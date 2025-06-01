package backend.academy.bot.config.kafka;

import backend.academy.base.schema.bot.LinkUpdate;
import backend.academy.bot.BotApplication;
import backend.academy.bot.kafka.LinkUpdateKafkaListener;
import backend.academy.bot.kafka.exception.KafkaErrorHandler;
import backend.academy.bot.kafka.exception.LinkUpdateTrace;
import backend.academy.bot.link.service.LinkUpdateService;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import java.util.Map;
import org.apache.kafka.clients.admin.AdminClientConfig;
import org.apache.kafka.clients.admin.NewTopic;
import org.apache.kafka.clients.consumer.ConsumerConfig;
import org.apache.kafka.clients.producer.ProducerConfig;
import org.apache.kafka.common.serialization.StringDeserializer;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.context.annotation.Bean;
import org.springframework.kafka.config.ConcurrentKafkaListenerContainerFactory;
import org.springframework.kafka.config.TopicBuilder;
import org.springframework.kafka.core.DefaultKafkaConsumerFactory;
import org.springframework.kafka.core.DefaultKafkaProducerFactory;
import org.springframework.kafka.core.KafkaAdmin;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.kafka.support.serializer.JsonDeserializer;
import org.springframework.validation.annotation.Validated;

@Validated
@ConditionalOnProperty(prefix = "app", name = "message-transport", havingValue = "Kafka")
@ConfigurationProperties(prefix = "kafka")
public record KafkaConfig(
        @NotBlank String topicName,
        @NotBlank String dlqTopicName,
        @NotNull Integer dlqPartitions,
        @NotNull Integer dlqReplicas,
        @NotBlank String bootstrapServers,
        @NotBlank String groupId,
        @NotNull Class<?> keyDeserializer,
        @NotNull Class<?> valueDeserializer,
        @NotNull Class<?> keySerializer,
        @NotNull Class<?> valueSerializer) {

    public static final String CONTAINER_FACTORY = "linkUpdateKafkaListenerContainerFactory";
    public static final String ERROR_HANDLER = "kafkaErrorHandler";

    private static final String TRUSTED_PACKAGE =
            BotApplication.class.getPackage().getName();

    @Bean
    public KafkaAdmin admin() {
        return new KafkaAdmin(Map.of(AdminClientConfig.BOOTSTRAP_SERVERS_CONFIG, bootstrapServers));
    }

    @Bean
    public LinkUpdateKafkaListener linkUpdateKafkaListener(final LinkUpdateService linkUpdateService) {
        return new LinkUpdateKafkaListener(linkUpdateService);
    }

    @Bean
    public ConcurrentKafkaListenerContainerFactory<String, LinkUpdate> linkUpdateKafkaListenerContainerFactory() {
        final ConcurrentKafkaListenerContainerFactory<String, LinkUpdate> factory =
                new ConcurrentKafkaListenerContainerFactory<>();

        final JsonDeserializer<Object> deserializer = new JsonDeserializer<>();
        deserializer.addTrustedPackages(TRUSTED_PACKAGE);
        deserializer.setUseTypeMapperForKey(false);

        factory.setConsumerFactory(new DefaultKafkaConsumerFactory<>(
                Map.of(
                        ConsumerConfig.BOOTSTRAP_SERVERS_CONFIG,
                        bootstrapServers,
                        ConsumerConfig.GROUP_ID_CONFIG,
                        groupId,
                        ConsumerConfig.KEY_DESERIALIZER_CLASS_CONFIG,
                        keyDeserializer,
                        ConsumerConfig.VALUE_DESERIALIZER_CLASS_CONFIG,
                        valueDeserializer),
                new StringDeserializer(),
                deserializer));

        return factory;
    }

    @Bean
    public KafkaErrorHandler kafkaErrorHandler() {
        return new KafkaErrorHandler(dlqTopicName, dlqKafkaTemplate());
    }

    @Bean
    public KafkaTemplate<String, LinkUpdateTrace> dlqKafkaTemplate() {
        return new KafkaTemplate<>(new DefaultKafkaProducerFactory<>(Map.of(
                ProducerConfig.BOOTSTRAP_SERVERS_CONFIG, bootstrapServers,
                ProducerConfig.KEY_SERIALIZER_CLASS_CONFIG, keySerializer,
                ProducerConfig.VALUE_SERIALIZER_CLASS_CONFIG, valueSerializer)));
    }

    @Bean
    public NewTopic dlqTopic() {
        return TopicBuilder.name(dlqTopicName)
                .partitions(dlqPartitions)
                .replicas(dlqReplicas)
                .build();
    }
}
