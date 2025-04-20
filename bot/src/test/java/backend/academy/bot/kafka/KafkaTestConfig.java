package backend.academy.bot.kafka;

import backend.academy.base.schema.bot.LinkUpdate;
import backend.academy.bot.BotTest;
import java.util.Map;
import org.apache.kafka.clients.admin.NewTopic;
import org.apache.kafka.clients.producer.ProducerConfig;
import org.apache.kafka.common.serialization.StringSerializer;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.kafka.config.TopicBuilder;
import org.springframework.kafka.core.DefaultKafkaProducerFactory;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.kafka.support.serializer.JsonSerializer;
import org.testcontainers.kafka.KafkaContainer;

@Configuration
public class KafkaTestConfig implements BotTest {

    private final KafkaContainer kafka = EnabledKafkaTest.kafka;

    @Bean
    public KafkaTemplate<String, LinkUpdate> scrapperKafkaTemplate() {
        return new KafkaTemplate<>(new DefaultKafkaProducerFactory<>(Map.of(
                ProducerConfig.BOOTSTRAP_SERVERS_CONFIG, kafka.getBootstrapServers(),
                ProducerConfig.KEY_SERIALIZER_CLASS_CONFIG, StringSerializer.class,
                ProducerConfig.VALUE_SERIALIZER_CLASS_CONFIG, JsonSerializer.class)));
    }

    @Bean
    public NewTopic topic() {
        return TopicBuilder.name(TOPIC).partitions(1).replicas(1).build();
    }
}
