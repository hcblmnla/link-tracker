package backend.academy.scrapper.config;

import backend.academy.base.schema.bot.LinkUpdate;
import backend.academy.scrapper.service.kafka.LinkUpdateKafkaProducer;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
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
import org.springframework.kafka.support.serializer.JsonSerializer;
import org.springframework.validation.annotation.Validated;

@Validated
@ConditionalOnProperty(prefix = "app.message-transport", name = "kafka", havingValue = "true")
@ConfigurationProperties(prefix = "kafka")
public record KafkaConfig(
        @NotBlank String topicName,
        @NotNull Integer partitions,
        @NotNull Integer replicas,
        @NotBlank String bootstrapServers) {

    @Bean
    public KafkaAdmin admin() {
        return new KafkaAdmin(Map.of(AdminClientConfig.BOOTSTRAP_SERVERS_CONFIG, bootstrapServers));
    }

    @Bean
    public LinkUpdateKafkaProducer linkUpdateKafkaProducer(final KafkaTemplate<String, LinkUpdate> kafkaTemplate) {
        return new LinkUpdateKafkaProducer(kafkaTemplate, topicName);
    }

    @Bean
    public KafkaTemplate<String, LinkUpdate> scrapperKafkaTemplate() {
        return new KafkaTemplate<>(new DefaultKafkaProducerFactory<>(Map.of(
                ProducerConfig.BOOTSTRAP_SERVERS_CONFIG, bootstrapServers,
                ProducerConfig.KEY_SERIALIZER_CLASS_CONFIG, StringSerializer.class,
                ProducerConfig.VALUE_SERIALIZER_CLASS_CONFIG, JsonSerializer.class)));
    }

    @Bean
    public NewTopic topic() {
        return TopicBuilder.name(topicName)
                .partitions(partitions)
                .replicas(replicas)
                .build();
    }
}
