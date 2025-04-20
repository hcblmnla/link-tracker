package backend.academy.bot.config;

import backend.academy.base.schema.bot.LinkUpdate;
import backend.academy.bot.link.service.LinkUpdateService;
import backend.academy.bot.service.kafka.LinkUpdateKafkaListener;
import backend.academy.bot.service.kafka.exception.KafkaErrorHandler;
import backend.academy.bot.service.kafka.exception.LinkUpdateTrace;
import jakarta.validation.Validator;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import java.util.Map;
import org.apache.kafka.clients.admin.AdminClientConfig;
import org.apache.kafka.clients.admin.NewTopic;
import org.apache.kafka.clients.consumer.ConsumerConfig;
import org.apache.kafka.clients.producer.ProducerConfig;
import org.apache.kafka.common.serialization.StringDeserializer;
import org.apache.kafka.common.serialization.StringSerializer;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.context.annotation.Bean;
import org.springframework.kafka.config.ConcurrentKafkaListenerContainerFactory;
import org.springframework.kafka.config.TopicBuilder;
import org.springframework.kafka.core.DefaultKafkaConsumerFactory;
import org.springframework.kafka.core.DefaultKafkaProducerFactory;
import org.springframework.kafka.core.KafkaAdmin;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.kafka.listener.CommonErrorHandler;
import org.springframework.kafka.support.serializer.JsonDeserializer;
import org.springframework.kafka.support.serializer.JsonSerializer;
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
        @NotBlank String groupId) {

    @Bean
    public KafkaAdmin admin() {
        return new KafkaAdmin(Map.of(AdminClientConfig.BOOTSTRAP_SERVERS_CONFIG, bootstrapServers));
    }

    @Bean
    public LinkUpdateKafkaListener linkUpdateKafkaListener(
            final LinkUpdateService linkUpdateService,
            final KafkaTemplate<String, LinkUpdateTrace> dlq,
            final Validator validator) {
        return new LinkUpdateKafkaListener(linkUpdateService, dlq, validator, dlqTopicName);
    }

    @Bean
    public ConcurrentKafkaListenerContainerFactory<String, LinkUpdate> linkUpdateKafkaListenerContainerFactory(
            final CommonErrorHandler commonErrorHandler) {
        final ConcurrentKafkaListenerContainerFactory<String, LinkUpdate> factory =
                new ConcurrentKafkaListenerContainerFactory<>();

        factory.setConsumerFactory(new DefaultKafkaConsumerFactory<>(
                Map.of(
                        ConsumerConfig.BOOTSTRAP_SERVERS_CONFIG,
                        bootstrapServers,
                        ConsumerConfig.GROUP_ID_CONFIG,
                        groupId,
                        ConsumerConfig.KEY_DESERIALIZER_CLASS_CONFIG,
                        StringDeserializer.class,
                        ConsumerConfig.VALUE_DESERIALIZER_CLASS_CONFIG,
                        JsonDeserializer.class),
                new StringDeserializer(),
                new JsonDeserializer<>(LinkUpdate.class)));

        factory.setCommonErrorHandler(commonErrorHandler);

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
                ProducerConfig.KEY_SERIALIZER_CLASS_CONFIG, StringSerializer.class,
                ProducerConfig.VALUE_SERIALIZER_CLASS_CONFIG, JsonSerializer.class)));
    }

    @Bean
    public NewTopic dlqTopic() {
        return TopicBuilder.name(dlqTopicName)
                .partitions(dlqPartitions)
                .replicas(dlqReplicas)
                .build();
    }
}
